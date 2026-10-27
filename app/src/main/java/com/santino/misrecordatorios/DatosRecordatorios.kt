package com.santino.misrecordatorios

import androidx.compose.ui.graphics.Color

object DatosRecordatorios {

    // LIST: recordatorios simulados
    val recordatorios: List<Recordatorio> = listOf(
        Recordatorio(
            id = 1,
            titulo = "Estudiar Kotlin",
            categoria = Categoria.Estudio,
            prioridad = Prioridad.Alta,
            estado = Estado.Pendiente,
            descripcion = "Repasar los conceptos de Kotlin"
        ),
        Recordatorio(
            id = 2,
            titulo = "Entregar actividad",
            categoria = Categoria.Academica,
            prioridad = Prioridad.Alta,
            estado = Estado.Pendiente,
            descripcion = "Presentar el trabajo de Android"
        ),
        Recordatorio(
            id = 3,
            titulo = "Revisar apuntes",
            categoria = Categoria.Personal,
            prioridad = Prioridad.Baja,
            estado = Estado.Completado
        ),
        Recordatorio(
            id = 4,
            titulo = "Practicar Jetpack Compose",
            categoria = Categoria.Estudio,
            prioridad = Prioridad.Media,
            estado = Estado.Pendiente
        ),
        Recordatorio(
            id = 5,
            titulo = "Preparar presentación",
            categoria = Categoria.Trabajo,
            prioridad = Prioridad.Media,
            estado = Estado.Completado
        ),
        Recordatorio(
            id = 6,
            titulo = "Organizar tareas",
            categoria = Categoria.Personal,
            prioridad = Prioridad.Baja,
            estado = Estado.Pendiente
        )
    )

    // SET: categorías sin duplicados
    val categorias: Set<Categoria> =
        recordatorios.map { it.categoria }.toSet()

    // MAP: asociar cada categoría con un color
    val coloresPorCategoria: Map<Categoria, Color> = mapOf(
        Categoria.Estudio to Color(0xFF6750A4),
        Categoria.Academica to Color(0xFF1976D2),
        Categoria.Personal to Color(0xFF388E3C),
        Categoria.Trabajo to Color(0xFFE65100)
    )

    // FILTROS: por estado, prioridad o ambos
    fun filtrar(
        estado: Estado? = null,
        prioridad: Prioridad? = null
    ): List<Recordatorio> {

        return recordatorios.filter { recordatorio ->
            (estado == null || recordatorio.estado == estado) &&
                    (prioridad == null || recordatorio.prioridad == prioridad)
        }
    }
}
