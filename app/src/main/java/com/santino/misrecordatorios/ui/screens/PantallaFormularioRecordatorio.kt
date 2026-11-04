package com.santino.misrecordatorios.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.santino.misrecordatorios.DatosRecordatorios
import com.santino.misrecordatorios.Prioridad
import com.santino.misrecordatorios.ui.viewmodel.RecordatoriosViewModel

@Composable
fun PantallaFormularioRecordatorio(
    viewModel: RecordatoriosViewModel,
    onCancelar: () -> Unit,
    onGuardadoExitoso: () -> Unit
) {
    val estado by viewModel.formularioUiState.collectAsStateWithLifecycle()

    // Las categorías disponibles se obtienen de los datos simulados.
    val categorias = remember {
        DatosRecordatorios.recordatorios.map { it.categoria }.distinct()
    }

    LaunchedEffect(estado.guardadoExitoso) {
        if (estado.guardadoExitoso) {
            onGuardadoExitoso()
        }
    }

    Scaffold { paddingInterior ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingInterior)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Nuevo recordatorio",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Completá los datos para registrar una tarea.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = estado.titulo,
                onValueChange = viewModel::cambiarTitulo,
                label = { Text("Título *") },
                singleLine = true,
                isError = estado.errorTitulo != null,
                supportingText = {
                    Text(estado.errorTitulo ?: "Mínimo 4 caracteres")
                },
                enabled = !estado.enviando,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estado.descripcion,
                onValueChange = viewModel::cambiarDescripcion,
                label = { Text("Descripción (opcional)") },
                minLines = 3,
                maxLines = 5,
                isError = estado.errorDescripcion != null,
                supportingText = {
                    Text(estado.errorDescripcion ?: "${estado.descripcion.length}/200 caracteres")
                },
                enabled = !estado.enviando,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Categoría *", fontWeight = FontWeight.SemiBold)

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categorias.forEach { categoria ->
                    FilterChip(
                        selected = estado.categoria == categoria,
                        onClick = { viewModel.seleccionarCategoria(categoria) },
                        label = { Text(categoria.nombre) },
                        enabled = !estado.enviando
                    )
                }
            }

            estado.errorCategoria?.let { mensaje ->
                Text(mensaje, color = MaterialTheme.colorScheme.error)
            }

            Text("Prioridad *", fontWeight = FontWeight.SemiBold)

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(Prioridad.Alta, Prioridad.Media, Prioridad.Baja).forEach { prioridad ->
                    FilterChip(
                        selected = estado.prioridad == prioridad,
                        onClick = { viewModel.seleccionarPrioridad(prioridad) },
                        label = { Text(prioridad.nombre) },
                        enabled = !estado.enviando
                    )
                }
            }

            estado.errorPrioridad?.let { mensaje ->
                Text(mensaje, color = MaterialTheme.colorScheme.error)
            }

            estado.errorGeneral?.let { mensaje ->
                Text(mensaje, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCancelar,
                    enabled = !estado.enviando
                ) {
                    Text("Cancelar")
                }

                Button(
                    onClick = viewModel::guardarRecordatorio,
                    enabled = estado.puedeGuardar
                ) {
                    if (estado.enviando) {
                        CircularProgressIndicator(
                            modifier = Modifier.height(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Guardar recordatorio")
                    }
                }
            }
        }
    }
}

