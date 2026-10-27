package com.santino.misrecordatorios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.santino.misrecordatorios.ui.theme.MisRecordatoriosTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MisRecordatoriosTheme {
                PantallaRecordatorios()
            }
        }
    }
}

@Composable
fun PantallaRecordatorios() {

    // Estado de los filtros
    var filtroEstado by remember {
        mutableStateOf<Estado?>(null)
    }

    var filtroPrioridad by remember {
        mutableStateOf<Prioridad?>(null)
    }

    // Obtener recordatorios filtrados
    val recordatoriosVisibles = DatosRecordatorios.filtrar(
        estado = filtroEstado,
        prioridad = filtroPrioridad
    )

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            // Título principal
            Text(
                text = "Mis Recordatorios",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Organizá tus tareas y actividades",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Filtro por estado
            Text(
                text = "Filtrar por estado",
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier.horizontalScroll(
                    rememberScrollState()
                ),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                FilterChip(
                    selected = filtroEstado == null,
                    onClick = { filtroEstado = null },
                    label = { Text("Todos") }
                )

                FilterChip(
                    selected = filtroEstado == Estado.Pendiente,
                    onClick = {
                        filtroEstado = Estado.Pendiente
                    },
                    label = { Text("Pendientes") }
                )

                FilterChip(
                    selected = filtroEstado == Estado.Completado,
                    onClick = {
                        filtroEstado = Estado.Completado
                    },
                    label = { Text("Completados") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Filtro por prioridad
            Text(
                text = "Filtrar por prioridad",
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier.horizontalScroll(
                    rememberScrollState()
                ),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                FilterChip(
                    selected = filtroPrioridad == null,
                    onClick = { filtroPrioridad = null },
                    label = { Text("Todas") }
                )

                FilterChip(
                    selected = filtroPrioridad == Prioridad.Alta,
                    onClick = {
                        filtroPrioridad = Prioridad.Alta
                    },
                    label = { Text("Alta") }
                )

                FilterChip(
                    selected = filtroPrioridad == Prioridad.Media,
                    onClick = {
                        filtroPrioridad = Prioridad.Media
                    },
                    label = { Text("Media") }
                )

                FilterChip(
                    selected = filtroPrioridad == Prioridad.Baja,
                    onClick = {
                        filtroPrioridad = Prioridad.Baja
                    },
                    label = { Text("Baja") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Contador
            Text(
                text = "${recordatoriosVisibles.size} de " +
                        "${DatosRecordatorios.recordatorios.size} recordatorios",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Lista de recordatorios
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {

                items(
                    items = recordatoriosVisibles,
                    key = { it.id }
                ) { recordatorio ->

                    TarjetaRecordatorio(recordatorio)
                }
            }
        }
    }
}

// Tarjeta individual de recordatorio
@Composable
fun TarjetaRecordatorio(recordatorio: Recordatorio) {

    val colorCategoria =
        DatosRecordatorios.coloresPorCategoria[recordatorio.categoria]
            ?: MaterialTheme.colorScheme.primary

    val colorPrioridad = when (recordatorio.prioridad) {
        Prioridad.Alta -> Color(0xFFB3261E)
        Prioridad.Media -> Color(0xFF9A6700)
        Prioridad.Baja -> Color(0xFF2E7D32)
    }

    val colorEstado = when (recordatorio.estado) {
        Estado.Pendiente -> Color(0xFF9A6700)
        Estado.Completado -> Color(0xFF2E7D32)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = recordatorio.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                Etiqueta(
                    texto = recordatorio.categoria.nombre,
                    color = colorCategoria
                )

                Etiqueta(
                    texto = recordatorio.prioridad.nombre,
                    color = colorPrioridad
                )

                Etiqueta(
                    texto = recordatorio.estado.nombre,
                    color = colorEstado
                )
            }

            // Mostrar descripción solo si existe
            recordatorio.descripcion?.let { descripcion ->

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

// Etiquetas de colores
@Composable
fun Etiqueta(
    texto: String,
    color: Color
) {

    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(50)
    ) {

        Text(
            text = texto,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 6.dp
            )
        )
    }
}

// Vista previa
@Preview(showBackground = true)
@Composable
fun PantallaRecordatoriosPreview() {

    MisRecordatoriosTheme {
        PantallaRecordatorios()
    }
}
