package com.rolloapp.app.ui

import com.rolloapp.app.domain.PaperPriceCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class FormattingTest {

    @Test
    fun `precio por hoja se muestra con decimales suficientes para cerrar contra el de 100 hojas`() {
        // $4500 / 16 rollos / 320 hojas -> 0.87890625 por hoja.
        // Con 2 decimales se mostraba "$0.88", y "$0.88 x 100" no da los "$87.89"
        // que muestra la fila de 100 hojas: parecía un error de cálculo.
        val desglose = PaperPriceCalculator.calcular(4500.0, 16, 320)!!

        assertEquals("$0.8789", formatoMoneda(desglose.precioPorHoja))
        assertEquals("$87.89", formatoMoneda(desglose.precioPor100Hojas))
    }

    @Test
    fun `importes de un peso o mas van con dos decimales`() {
        assertEquals("$281.25", formatoMoneda(281.25))
        assertEquals("$1.38", formatoMoneda(1.375))
        assertEquals("$4500.00", formatoMonedaCorta(4500.0))
    }

    @Test
    fun `importes muy chicos conservan precision`() {
        assertEquals("$0.0040", formatoMoneda(0.003993333))
        assertEquals("$0.000050", formatoMoneda(0.00005))
    }

    @Test
    fun `valores no finitos se muestran como guion`() {
        assertEquals("—", formatoMoneda(Double.NaN))
        assertEquals("—", formatoMoneda(Double.POSITIVE_INFINITY))
        assertEquals("—", formatoMonedaCorta(Double.NaN))
    }
}
