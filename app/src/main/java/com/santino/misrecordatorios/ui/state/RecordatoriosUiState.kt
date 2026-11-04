package com.santino.misrecordatorios.ui.state

import com.santino.misrecordatorios.Categoria
import com.santino.misrecordatorios.Prioridad
import com.santino.misrecordatorios.Recordatorio

// Estados de la pantalla de listado
sealed interface ListadoUiState {

    data object Cargando : ListadoUiState

    data object Vacio : ListadoUiState

    data class ConDatos(
        val recordatorios: List<Recordatorio>
    ) : ListadoUiState

    data class Error(
        val mensaje: String
    ) : ListadoUiState
}

// Estados y valores del formulario de creación
data class FormularioUiState(

    val titulo: String = "",
    val descripcion: String = "",

    val categoria: Categoria? = null,
    val prioridad: Prioridad? = null,

    val errorTitulo: String? = null,
    val errorDescripcion: String? = null,
    val errorCategoria: String? = null,
    val errorPrioridad: String? = null,

    val enviando: Boolean = false,
    val guardadoExitoso: Boolean = false,
    val errorGeneral: String? = null

) {

    // Comprobar que los campos sean válidos
    val esValido: Boolean
        get() = titulo.trim().length >= 4 &&
                descripcion.length <= 200 &&
                categoria != null &&
                prioridad != null

    // El guardado también requiere que no exista un envío en curso
    val puedeGuardar: Boolean
        get() = esValido && !enviando && !guardadoExitoso
}

// Estados de la pantalla de detalle
sealed interface DetalleUiState {

    data object Cargando : DetalleUiState

    data class ConDatos(
        val recordatorio: Recordatorio
    ) : DetalleUiState

    data class Error(
        val mensaje: String
    ) : DetalleUiState
}