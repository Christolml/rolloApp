package com.rolloapp.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Un paquete de papel higiénico guardado en el historial.
 *
 * @param esSimulado `true` cuando la entrada no corresponde a un producto real sino
 *   a una simulación guardada desde la pantalla de detalle.
 * @param fotoPath ruta absoluta a la foto del paquete en el almacenamiento privado
 *   de la app (`filesDir/photos/...`), o `null` si no se tomó ninguna. Cada fila es
 *   dueña exclusiva de su propio archivo — ver `AlmacenFotos`.
 */
@Entity(tableName = "paper_packages")
data class PaperPackage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val marca: String,
    val precio: Double,
    val rollosPorPaquete: Int,
    val hojasPorRollo: Int,
    val fecha: Long = System.currentTimeMillis(),
    val esSimulado: Boolean = false,
    val fotoPath: String? = null,
)
