package com.santino.misrecordatorios.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.santino.misrecordatorios.ui.state.DetalleUiState
import com.santino.misrecordatorios.ui.viewmodel.RecordatoriosViewModel

@Composable
fun PantallaDetalleRecordatorio(
    id: Int,
    viewModel: RecordatoriosViewModel,
    onVolver: () -> Unit
) {

    val estado by viewModel.detalleUiState.collectAsStateWithLifecycle()

    // Buscar el recordatorio por su identificador
    LaunchedEffect(id) {
        viewModel.cargarDetalle(id)
    }

    Scaffold { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            TextButton(
                onClick = onVolver
            ) {
                Text("← Volver")
            }

            Text(
                text = "Detalle del recordatorio",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            when (val detalle = estado) {

                DetalleUiState.Cargando -> {

                    CircularProgressIndicator()

                    Text("Cargando información...")
                }

                is DetalleUiState.Error -> {

                    Text(
                        text = detalle.mensaje,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                is DetalleUiState.ConDatos -> {

                    val recordatorio = detalle.recordatorio

                    // Evitar mostrar un detalle anterior
                    // mientras se consulta otro identificador
                    if (recordatorio.id != id) {

                        CircularProgressIndicator()

                    } else {

                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {

                                Text(
                                    text = recordatorio.titulo,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )

                                Text(
                                    text = "Categoría: ${recordatorio.categoria.nombre}"
                                )

                                Text(
                                    text = "Prioridad: ${recordatorio.prioridad.nombre}"
                                )

                                Text(
                                    text = "Estado: ${recordatorio.estado.nombre}"
                                )

                                Text(
                                    text = "Descripción:",
                                    fontWeight = FontWeight.SemiBold
                                )

                                Text(
                                    text = recordatorio.descripcion
                                        ?: "Sin descripción"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
