package com.rolloapp.app.data

import kotlinx.coroutines.flow.Flow

/**
 * Única puerta de entrada a la persistencia. Se construye una sola vez en
 * `RolloApplication` y se le pasa al ViewModel con una factory manual.
 */
class PaperRepository(private val dao: PaperPackageDao) {

    /** Historial completo, observable. */
    val paquetes: Flow<List<PaperPackage>> = dao.observeAll()

    fun observeById(id: Long): Flow<PaperPackage?> = dao.observeById(id)

    suspend fun insert(paquete: PaperPackage): Long = dao.insert(paquete)

    suspend fun insert(
        marca: String,
        precio: Double,
        rollosPorPaquete: Int,
        hojasPorRollo: Int,
        esSimulado: Boolean = false,
    ): Long = dao.insert(
        PaperPackage(
            marca = marca,
            precio = precio,
            rollosPorPaquete = rollosPorPaquete,
            hojasPorRollo = hojasPorRollo,
            esSimulado = esSimulado,
        ),
    )

    suspend fun delete(paquete: PaperPackage) = dao.delete(paquete)

    suspend fun deleteById(id: Long) = dao.deleteById(id)
}
