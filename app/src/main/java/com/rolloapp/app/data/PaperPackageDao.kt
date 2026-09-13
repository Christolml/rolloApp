package com.rolloapp.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PaperPackageDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(paquete: PaperPackage): Long

    @Delete
    suspend fun delete(paquete: PaperPackage)

    @Query("DELETE FROM paper_packages WHERE id = :id")
    suspend fun deleteById(id: Long)

    /** Historial completo. El orden final por precio por hoja se hace en el ViewModel. */
    @Query("SELECT * FROM paper_packages ORDER BY fecha DESC")
    fun observeAll(): Flow<List<PaperPackage>>

    @Query("SELECT * FROM paper_packages WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<PaperPackage?>
}
