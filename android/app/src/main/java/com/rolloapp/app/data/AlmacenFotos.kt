package com.rolloapp.app.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

/**
 * Fotos de paquetes: almacenamiento privado de la app (`filesDir/photos`), nunca
 * la galería. No requiere permisos de almacenamiento en ningún nivel de API y se
 * borra solo si se desinstala la app.
 *
 * Usa `android.media.ExifInterface` (framework, disponible desde API 1) en vez de
 * `androidx.exifinterface`, para no sumar una dependencia nueva: como siempre
 * escribimos JPEG estándar generado por la propia cámara del dispositivo, la clase
 * del framework alcanza.
 */
object AlmacenFotos {

    private const val CARPETA = "photos"
    private const val LADO_MAXIMO_PX = 2048
    private const val CALIDAD_JPEG = 92

    private fun carpetaFotos(context: Context): File =
        File(context.filesDir, CARPETA).apply { mkdirs() }

    /**
     * Crea el `File` (todavía sin contenido) que va a recibir la foto. Llamar ANTES
     * de lanzar el launcher de `TakePicture()` — la cámara escribe el archivo al
     * abrir el `OutputStream` sobre la `Uri`, no hace falta que ya tenga bytes.
     */
    fun crearArchivoNuevo(context: Context): File =
        File(carpetaFotos(context), "foto_${UUID.randomUUID()}.jpg")

    /** `Uri` de tipo `content://` vía FileProvider: la única que acepta la cámara del sistema. */
    fun uriParaArchivo(context: Context, archivo: File): Uri =
        FileProvider.getUriForFile(context, context.packageName + ".fileprovider", archivo)

    /**
     * Downsamplea y comprime EN EL LUGAR la foto recién escrita por la cámara, y
     * corrige la orientación EXIF rotando los píxeles (no solo el tag), así el resto
     * de la app nunca necesita volver a pensar en orientación. Devuelve `false` si
     * el archivo no se pudo procesar (por ejemplo, la cámara no llegó a escribir
     * nada); en ese caso el llamador debe borrar el archivo y no guardar `fotoPath`.
     */
    fun downsamplearEnElLugar(archivo: File): Boolean {
        if (!archivo.exists() || archivo.length() == 0L) return false

        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(archivo.absolutePath, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return false

        val opciones = BitmapFactory.Options().apply {
            inSampleSize = calcularInSampleSize(bounds.outWidth, bounds.outHeight, LADO_MAXIMO_PX)
        }
        val bitmapDecodificado = BitmapFactory.decodeFile(archivo.absolutePath, opciones)
            ?: return false

        val orientacion = runCatching {
            ExifInterface(archivo.absolutePath)
                .getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        }.getOrDefault(ExifInterface.ORIENTATION_NORMAL)

        val bitmapFinal = aplicarRotacionExif(bitmapDecodificado, orientacion)

        return try {
            FileOutputStream(archivo).use { salida ->
                bitmapFinal.compress(Bitmap.CompressFormat.JPEG, CALIDAD_JPEG, salida)
            }
            true
        } catch (e: IOException) {
            false
        } finally {
            if (bitmapFinal !== bitmapDecodificado) bitmapDecodificado.recycle()
            bitmapFinal.recycle()
        }
    }

    private fun calcularInSampleSize(anchoOriginal: Int, altoOriginal: Int, ladoMaximo: Int): Int {
        var inSampleSize = 1
        var ancho = anchoOriginal
        var alto = altoOriginal
        while (ancho / 2 >= ladoMaximo || alto / 2 >= ladoMaximo) {
            ancho /= 2
            alto /= 2
            inSampleSize *= 2
        }
        return inSampleSize
    }

    private fun aplicarRotacionExif(bitmap: Bitmap, orientacion: Int): Bitmap {
        val grados = when (orientacion) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> return bitmap
        }
        val matriz = Matrix().apply { postRotate(grados) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matriz, true)
    }

    /**
     * Copia el archivo de foto de una entrada a un archivo propio nuevo. Se usa al
     * guardar una simulación: así la simulación tiene SU PROPIO archivo, no una
     * referencia compartida, y se puede borrar cualquiera de las dos entradas
     * (original o simulada) sin romper a la otra.
     */
    fun copiar(context: Context, fotoPathOriginal: String?): String? {
        if (fotoPathOriginal.isNullOrBlank()) return null
        val origen = File(fotoPathOriginal)
        if (!origen.exists()) return null
        val destino = crearArchivoNuevo(context)
        return try {
            origen.copyTo(destino, overwrite = true)
            destino.absolutePath
        } catch (e: IOException) {
            null
        }
    }

    /** Borra el archivo de foto asociado a una entrada, si existe. No falla si ya no está. */
    fun borrar(fotoPath: String?) {
        if (fotoPath.isNullOrBlank()) return
        runCatching { File(fotoPath).delete() }
    }
}
