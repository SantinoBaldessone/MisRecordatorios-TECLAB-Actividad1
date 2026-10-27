package com.santino.misrecordatorios

// Categorías disponibles
sealed class Categoria(val nombre: String) {
    object Estudio : Categoria("Estudio")
    object Academica : Categoria("Académica")
    object Personal : Categoria("Personal")
    object Trabajo : Categoria("Trabajo")
}

// Prioridades disponibles
sealed class Prioridad(val nombre: String) {
    object Alta : Prioridad("Alta")
    object Media : Prioridad("Media")
    object Baja : Prioridad("Baja")
}

// Estados disponibles
sealed class Estado(val nombre: String) {
    object Pendiente : Estado("Pendiente")
    object Completado : Estado("Completado")
}

// Modelo de cada recordatorio
data class Recordatorio(
val id: Int,
val titulo: String,
val categoria: Categoria,
val prioridad: Prioridad,
val estado: Estado,
val descripcion: String? = null
) {
    init {
        require(id > 0) {
            "El identificador debe ser mayor que cero"
        }

        require(titulo.isNotBlank()) {
            "El título no puede estar vacío"
        }

        require(descripcion == null || descripcion.isNotBlank()) {
            "La descripción no puede estar vacía"
        }
    }
}

