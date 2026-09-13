package com.rolloapp.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.rolloapp.app.data.PaperPackage
import com.rolloapp.app.data.PaperRepository
import com.rolloapp.app.domain.PaperPriceCalculator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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
    val precioPorRollo: Double,
    val precioPorHoja: Double,
    val precioPor100Hojas: Double,
) {
    val totalHojas: Int get() = rollosPorPaquete * hojasPorRollo
}

class PaperViewModel(private val repository: PaperRepository) : ViewModel() {

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
            )
        }
    }

    fun eliminar(entrada: PaperEntryUi) {
        viewModelScope.launch { repository.deleteById(entrada.id) }
    }

    companion object {
        /** Factory manual: sin Hilt, el repositorio llega desde `RolloApplication`. */
        fun factory(repository: PaperRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { PaperViewModel(repository) }
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
                precioPorRollo = desglose.precioPorRollo,
                precioPorHoja = desglose.precioPorHoja,
                precioPor100Hojas = desglose.precioPor100Hojas,
            )
        }
    }
}
