package com.rolloapp.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.rolloapp.app.data.AlmacenFotos
import com.rolloapp.app.data.PaperPackage
import com.rolloapp.app.data.PaperRepository
import com.rolloapp.app.domain.PaperPriceCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Una entrada del historial junto con sus precios unitarios ya calculados.
 */
data class PaperEntryUi(
    val id: Long,
    val marca: String,
    val precio: Double,
    val rollosPorPaquete: Int,
    val hojasPorRollo: Int,
    val fecha: Long,
    val esSimulado: Boolean,
    val fotoPath: String?,
    val precioPorRollo: Double,
    val precioPorHoja: Double,
    val precioPor100Hojas: Double,
) {
    val totalHojas: Int get() = rollosPorPaquete * hojasPorRollo
}

/**
 * `AndroidViewModel` (no `ViewModel` a secas) porque `AlmacenFotos` necesita un
 * `Context` para leer/escribir en `filesDir` — sigue sin haber DI, el `Application`
 * llega por el mismo mecanismo que ya provee `getApplication()`.
 */
class PaperViewModel(
    application: Application,
    private val repository: PaperRepository,
) : AndroidViewModel(application) {

    /**
     * Historial completo ordenado por precio por hoja ascendente: el primer
     * elemento es siempre la opción más conveniente.
     */
    val entradas: StateFlow<List<PaperEntryUi>> = repository.paquetes
        .map { paquetes -> paquetes.mapNotNull(::aEntradaUi).sortedBy { it.precioPorHoja } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    fun guardar(
        marca: String,
        precio: Double,
        rollosPorPaquete: Int,
        hojasPorRollo: Int,
        esSimulado: Boolean = false,
        fotoPath: String? = null,
    ) {
        if (!PaperPriceCalculator.esEntradaValida(precio, rollosPorPaquete, hojasPorRollo)) return
        val nombre = marca.trim().ifBlank { "Sin marca" }
        viewModelScope.launch {
            repository.insert(
                marca = nombre,
                precio = precio,
                rollosPorPaquete = rollosPorPaquete,
                hojasPorRollo = hojasPorRollo,
                esSimulado = esSimulado,
                fotoPath = fotoPath,
            )
        }
    }

    /**
     * Guarda una simulación heredando la foto de la entrada original, pero en un
     * archivo propio (copia física, no referencia compartida): así se puede borrar
     * la simulación o el original de forma independiente sin dejar a la otra
     * entrada con una foto rota.
     */
    fun guardarSimulacion(entrada: PaperEntryUi, hojasHipoteticas: Int, precioSimulado: Double) {
        viewModelScope.launch {
            val fotoCopia = withContext(Dispatchers.IO) {
                AlmacenFotos.copiar(getApplication(), entrada.fotoPath)
            }
            guardar(
                marca = "${entrada.marca} ($hojasHipoteticas hojas)",
                precio = precioSimulado,
                rollosPorPaquete = entrada.rollosPorPaquete,
                hojasPorRollo = hojasHipoteticas,
                esSimulado = true,
                fotoPath = fotoCopia,
            )
        }
    }

    fun eliminar(entrada: PaperEntryUi) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { AlmacenFotos.borrar(entrada.fotoPath) }
            repository.deleteById(entrada.id)
        }
    }

    companion object {
        /** Factory manual: sin Hilt, el repositorio llega desde `RolloApplication`. */
        fun factory(application: Application, repository: PaperRepository): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { PaperViewModel(application, repository) }
            }

        private fun aEntradaUi(paquete: PaperPackage): PaperEntryUi? {
            val desglose = PaperPriceCalculator.calcular(
                precio = paquete.precio,
                rollosPorPaquete = paquete.rollosPorPaquete,
                hojasPorRollo = paquete.hojasPorRollo,
            ) ?: return null

            return PaperEntryUi(
                id = paquete.id,
                marca = paquete.marca,
                precio = paquete.precio,
                rollosPorPaquete = paquete.rollosPorPaquete,
                hojasPorRollo = paquete.hojasPorRollo,
                fecha = paquete.fecha,
                esSimulado = paquete.esSimulado,
                fotoPath = paquete.fotoPath,
                precioPorRollo = desglose.precioPorRollo,
                precioPorHoja = desglose.precioPorHoja,
                precioPor100Hojas = desglose.precioPor100Hojas,
            )
        }
    }
}
