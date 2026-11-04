package com.santino.misrecordatorios.data

import com.santino.misrecordatorios.DatosRecordatorios
import com.santino.misrecordatorios.Recordatorio

// Contrato que debe cumplir cualquier repositorio
interface RepositorioRecordatorios {

    suspend fun obtenerTodos(): List<Recordatorio>

    suspend fun obtenerPorId(id: Int): Recordatorio?

    suspend fun guardar(recordatorio: Recordatorio)
}

// Implementación que almacena los datos en memoria
class RepositorioRecordatoriosEnMemoria :
    RepositorioRecordatorios {

    private val recordatorios =
        DatosRecordatorios.recordatorios.toMutableList()

    override suspend fun obtenerTodos(): List<Recordatorio> {
        return recordatorios.toList()
    }

    override suspend fun obtenerPorId(
        id: Int
    ): Recordatorio? {

        return recordatorios.find { it.id == id }
    }

    override suspend fun guardar(
        recordatorio: Recordatorio
    ) {

        require(recordatorios.none { it.id == recordatorio.id }) {
            "Ya existe un recordatorio con ese identificador"
        }

        recordatorios.add(recordatorio)
    }
}
