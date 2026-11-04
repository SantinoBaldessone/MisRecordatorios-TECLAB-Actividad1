package com.santino.misrecordatorios.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.santino.misrecordatorios.Categoria
import com.santino.misrecordatorios.Estado
import com.santino.misrecordatorios.Prioridad
import com.santino.misrecordatorios.Recordatorio
import com.santino.misrecordatorios.data.RepositorioRecordatorios
import com.santino.misrecordatorios.ui.state.DetalleUiState
import com.santino.misrecordatorios.ui.state.FormularioUiState
import com.santino.misrecordatorios.ui.state.ListadoUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecordatoriosViewModel(
    private val repositorio: RepositorioRecordatorios
) : ViewModel() {

    // Estado privado y modificable del listado
    private val _listadoUiState =
        MutableStateFlow<ListadoUiState>(ListadoUiState.Cargando)

    // Estado público de solo lectura
    val listadoUiState: StateFlow<ListadoUiState> =
        _listadoUiState.asStateFlow()

    // Estado de la pantalla de detalle
    private val _detalleUiState =
        MutableStateFlow<DetalleUiState>(DetalleUiState.Cargando)

    val detalleUiState: StateFlow<DetalleUiState> =
        _detalleUiState.asStateFlow()

    // Estado interno del formulario
    private val _formularioUiState =
        MutableStateFlow(FormularioUiState())

    // Estado público de solo lectura
    val formularioUiState: StateFlow<FormularioUiState> =
        _formularioUiState.asStateFlow()

    // Permite simular un error de guardado
    private var fallarSiguienteGuardado = false

    // Opciones de simulación para las pruebas
    private enum class ModoListado {
        NORMAL,
        VACIO,
        ERROR
    }

    private var modoListado = ModoListado.NORMAL
    private var trabajoCarga: Job? = null

    init {
        cargarRecordatorios()
    }

    // Cargar los recordatorios
    fun cargarRecordatorios() {

        trabajoCarga?.cancel()

        trabajoCarga = viewModelScope.launch {

            _listadoUiState.value = ListadoUiState.Cargando

            // Simular una operación asincrónica
            delay(700)

            when (modoListado) {

                ModoListado.VACIO -> {
                    _listadoUiState.value = ListadoUiState.Vacio
                }

                ModoListado.ERROR -> {
                    _listadoUiState.value = ListadoUiState.Error(
                        "No se pudieron cargar los recordatorios."
                    )
                }

                ModoListado.NORMAL -> {

                    try {
                        val datos = repositorio.obtenerTodos()

                        _listadoUiState.value =
                            if (datos.isEmpty()) {
                                ListadoUiState.Vacio
                            } else {
                                ListadoUiState.ConDatos(datos)
                            }

                    } catch (e: Exception) {

                        _listadoUiState.value = ListadoUiState.Error(
                            "Ocurrió un error al obtener los recordatorios."
                        )
                    }
                }
            }
        }
    }

    // Simular una lista sin recordatorios
    fun simularVacio() {
        modoListado = ModoListado.VACIO
        cargarRecordatorios()
    }

    // Simular un error de carga
    fun simularError() {
        modoListado = ModoListado.ERROR
        cargarRecordatorios()
    }

    // Restaurar los datos normales
    fun restaurarListado() {
        modoListado = ModoListado.NORMAL
        cargarRecordatorios()
    }

    // Reintentar después de un error
    fun reintentar() {
        restaurarListado()
    }

    // Buscar el detalle mediante su identificador
    fun cargarDetalle(id: Int) {

        viewModelScope.launch {

            _detalleUiState.value = DetalleUiState.Cargando

            delay(300)

            try {
                val recordatorio = repositorio.obtenerPorId(id)

                _detalleUiState.value =
                    if (recordatorio != null) {
                        DetalleUiState.ConDatos(recordatorio)
                    } else {
                        DetalleUiState.Error(
                            "No se encontró el recordatorio solicitado."
                        )
                    }

            } catch (e: Exception) {

                _detalleUiState.value = DetalleUiState.Error(
                    "No se pudo consultar el recordatorio."
                )
            }
        }
    }

    // Actualizar el título
    fun cambiarTitulo(valor: String) {

        _formularioUiState.update { actual ->

            actual.copy(
                titulo = valor,
                errorTitulo = if (valor.trim().length < 4) {
                    "El título debe tener al menos 4 caracteres"
                } else {
                    null
                },
                errorGeneral = null
            )
        }
    }

    // Actualizar la descripción
    fun cambiarDescripcion(valor: String) {

        _formularioUiState.update { actual ->

            actual.copy(
                descripcion = valor,
                errorDescripcion = if (valor.length > 200) {
                    "La descripción no puede superar 200 caracteres"
                } else {
                    null
                },
                errorGeneral = null
            )
        }
    }

    // Seleccionar categoría
    fun seleccionarCategoria(categoria: Categoria) {

        _formularioUiState.update {
            it.copy(
                categoria = categoria,
                errorCategoria = null,
                errorGeneral = null
            )
        }
    }

    // Seleccionar prioridad
    fun seleccionarPrioridad(prioridad: Prioridad) {

        _formularioUiState.update {
            it.copy(
                prioridad = prioridad,
                errorPrioridad = null,
                errorGeneral = null
            )
        }
    }

    // Activar un fallo simulado para la próxima operación
    fun simularFalloGuardado() {
        fallarSiguienteGuardado = true
    }

    // Guardar un recordatorio
    fun guardarRecordatorio() {

        val actual = _formularioUiState.value

        if (actual.enviando || actual.guardadoExitoso) {
            return
        }

        // Validar los campos
        val errorTitulo = if (actual.titulo.trim().length < 4) {
            "El título debe tener al menos 4 caracteres"
        } else {
            null
        }

        val errorDescripcion = if (actual.descripcion.length > 200) {
            "La descripción no puede superar 200 caracteres"
        } else {
            null
        }

        val errorCategoria = if (actual.categoria == null) {
            "Seleccioná una categoría"
        } else {
            null
        }

        val errorPrioridad = if (actual.prioridad == null) {
            "Seleccioná una prioridad"
        } else {
            null
        }

        // Actualizar los mensajes de validación
        _formularioUiState.update {
            it.copy(
                errorTitulo = errorTitulo,
                errorDescripcion = errorDescripcion,
                errorCategoria = errorCategoria,
                errorPrioridad = errorPrioridad
            )
        }

        // No guardar si hay campos inválidos
        if (
            errorTitulo != null ||
            errorDescripcion != null ||
            errorCategoria != null ||
            errorPrioridad != null
        ) {
            return
        }

        val categoria = actual.categoria ?: return
        val prioridad = actual.prioridad ?: return

        // Deshabilitar el guardado mientras se procesa
        _formularioUiState.update {
            it.copy(
                enviando = true,
                errorGeneral = null
            )
        }

        viewModelScope.launch {

            // Simular una operación asincrónica
            delay(700)

            try {

                if (fallarSiguienteGuardado) {
                    fallarSiguienteGuardado = false
                    error("Error de guardado simulado")
                }

                // Generar el siguiente identificador
                val nuevoId = (
                        repositorio.obtenerTodos()
                            .maxOfOrNull { it.id } ?: 0
                        ) + 1

                // Crear un recordatorio inmutable
                val nuevoRecordatorio = Recordatorio(
                    id = nuevoId,
                    titulo = actual.titulo.trim(),
                    descripcion = actual.descripcion.trim()
                        .takeIf { it.isNotEmpty() },
                    categoria = categoria,
                    prioridad = prioridad,
                    estado = Estado.Pendiente
                )

                // Guardar mediante la interfaz del repositorio
                repositorio.guardar(nuevoRecordatorio)

                modoListado = ModoListado.NORMAL

                // Comunicar el resultado exitoso
                _formularioUiState.update {
                    it.copy(
                        enviando = false,
                        guardadoExitoso = true,
                        errorGeneral = null
                    )
                }

                // Actualizar el listado
                cargarRecordatorios()

            } catch (e: Exception) {

                _formularioUiState.update {
                    it.copy(
                        enviando = false,
                        errorGeneral = "No se pudo guardar el recordatorio."
                    )
                }
            }
        }
    }

    // Restablecer el formulario para una nueva creación
    fun limpiarFormulario() {

        fallarSiguienteGuardado = false

        _formularioUiState.value = FormularioUiState()
    }

}

// Factory para proporcionar el repositorio al ViewModel
class RecordatoriosViewModelFactory(
    private val repositorio: RepositorioRecordatorios
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(
                RecordatoriosViewModel::class.java
            )
        ) {
            @Suppress("UNCHECKED_CAST")
            return RecordatoriosViewModel(repositorio) as T
        }

        throw IllegalArgumentException(
            "Tipo de ViewModel no reconocido"
        )
    }
}

