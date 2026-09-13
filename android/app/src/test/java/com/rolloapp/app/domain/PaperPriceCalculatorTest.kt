package com.rolloapp.app.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PaperPriceCalculatorTest {

    private val delta = 1e-9

    // ---------------------------------------------------------------- cálculo

    @Test
    fun `precioPorRollo divide el precio entre los rollos del paquete`() {
        assertEquals(3.0, PaperPriceCalculator.precioPorRollo(precio = 12.0, rollosPorPaquete = 4), delta)
    }

    @Test
    fun `precioPorHoja divide el precio por rollo entre las hojas del rollo`() {
        assertEquals(0.01, PaperPriceCalculator.precioPorHoja(precioPorRollo = 3.0, hojasPorRollo = 300), delta)
    }

    @Test
    fun `precioPor100Hojas multiplica el precio por hoja por cien`() {
        assertEquals(1.0, PaperPriceCalculator.precioPor100Hojas(precioPorHoja = 0.01), delta)
    }

    @Test
    fun `calcular devuelve el desglose completo de un paquete tipico`() {
        // 12.00 por 4 rollos de 300 hojas.
        val resultado = PaperPriceCalculator.calcular(
            precio = 12.0,
            rollosPorPaquete = 4,
            hojasPorRollo = 300,
        )

        assertTrue(resultado != null)
        requireNotNull(resultado)
        assertEquals(3.0, resultado.precioPorRollo, delta)
        assertEquals(0.01, resultado.precioPorHoja, delta)
        assertEquals(1.0, resultado.precioPor100Hojas, delta)
    }

    @Test
    fun `calcular soporta decimales no exactos`() {
        // 5.99 por 6 rollos de 250 hojas.
        val resultado = requireNotNull(
            PaperPriceCalculator.calcular(precio = 5.99, rollosPorPaquete = 6, hojasPorRollo = 250),
        )

        val esperadoPorRollo = 5.99 / 6
        val esperadoPorHoja = esperadoPorRollo / 250

        assertEquals(esperadoPorRollo, resultado.precioPorRollo, delta)
        assertEquals(esperadoPorHoja, resultado.precioPorHoja, delta)
        assertEquals(esperadoPorHoja * 100, resultado.precioPor100Hojas, delta)
    }

    @Test
    fun `calcular con un solo rollo deja el precio del paquete como precio por rollo`() {
        val resultado = requireNotNull(
            PaperPriceCalculator.calcular(precio = 2.5, rollosPorPaquete = 1, hojasPorRollo = 500),
        )

        assertEquals(2.5, resultado.precioPorRollo, delta)
        assertEquals(0.005, resultado.precioPorHoja, delta)
        assertEquals(0.5, resultado.precioPor100Hojas, delta)
    }

    // ------------------------------------------------------ entradas inválidas

    @Test
    fun `calcular devuelve null si no hay rollos`() {
        assertNull(PaperPriceCalculator.calcular(precio = 10.0, rollosPorPaquete = 0, hojasPorRollo = 200))
    }

    @Test
    fun `calcular devuelve null si no hay hojas`() {
        assertNull(PaperPriceCalculator.calcular(precio = 10.0, rollosPorPaquete = 4, hojasPorRollo = 0))
    }

    @Test
    fun `calcular devuelve null con precio negativo`() {
        assertNull(PaperPriceCalculator.calcular(precio = -1.0, rollosPorPaquete = 4, hojasPorRollo = 200))
    }

    @Test
    fun `calcular devuelve null con valores no finitos`() {
        assertNull(PaperPriceCalculator.calcular(precio = Double.NaN, rollosPorPaquete = 4, hojasPorRollo = 200))
        assertNull(
            PaperPriceCalculator.calcular(
                precio = Double.POSITIVE_INFINITY,
                rollosPorPaquete = 4,
                hojasPorRollo = 200,
            ),
        )
    }

    @Test
    fun `esEntradaValida distingue datos usables de datos incompletos`() {
        assertTrue(PaperPriceCalculator.esEntradaValida(12.0, 4, 300))
        assertTrue(PaperPriceCalculator.esEntradaValida(0.0, 1, 1))
        assertFalse(PaperPriceCalculator.esEntradaValida(12.0, 0, 300))
        assertFalse(PaperPriceCalculator.esEntradaValida(12.0, 4, 0))
        assertFalse(PaperPriceCalculator.esEntradaValida(-0.01, 4, 300))
    }

    // ------------------------------------------------------------- simulación

    @Test
    fun `simular calcula el precio de rollo y de paquete con otras hojas`() {
        // Paquete real: 12.00 / 4 rollos / 300 hojas -> 0.01 por hoja.
        val base = requireNotNull(
            PaperPriceCalculator.calcular(precio = 12.0, rollosPorPaquete = 4, hojasPorRollo = 300),
        )

        // ¿Cuánto costaría si trajera 500 hojas por rollo?
        val simulacion = requireNotNull(
            PaperPriceCalculator.simular(
                precioPorHoja = base.precioPorHoja,
                hojasHipoteticas = 500,
                rollosPorPaquete = 4,
            ),
        )

        assertEquals(500, simulacion.hojasHipoteticas)
        assertEquals(5.0, simulacion.precioPorRolloSimulado, delta)
        assertEquals(20.0, simulacion.precioPaqueteSimulado, delta)
    }

    @Test
    fun `simular con las mismas hojas reproduce los valores originales`() {
        val base = requireNotNull(
            PaperPriceCalculator.calcular(precio = 9.0, rollosPorPaquete = 3, hojasPorRollo = 200),
        )

        val simulacion = requireNotNull(
            PaperPriceCalculator.simular(
                precioPorHoja = base.precioPorHoja,
                hojasHipoteticas = 200,
                rollosPorPaquete = 3,
            ),
        )

        assertEquals(base.precioPorRollo, simulacion.precioPorRolloSimulado, delta)
        assertEquals(9.0, simulacion.precioPaqueteSimulado, delta)
    }

    @Test
    fun `simular permite comparar dos marcas con distinto conteo de hojas`() {
        // Marca A: 10.00 / 4 rollos / 200 hojas.
        val marcaA = requireNotNull(
            PaperPriceCalculator.calcular(precio = 10.0, rollosPorPaquete = 4, hojasPorRollo = 200),
        )
        // Marca B real: 14.00 / 4 rollos / 300 hojas.
        val marcaB = requireNotNull(
            PaperPriceCalculator.calcular(precio = 14.0, rollosPorPaquete = 4, hojasPorRollo = 300),
        )

        // Llevamos la marca A al formato de la marca B (300 hojas por rollo).
        val marcaAAjustada = requireNotNull(
            PaperPriceCalculator.simular(
                precioPorHoja = marcaA.precioPorHoja,
                hojasHipoteticas = 300,
                rollosPorPaquete = 4,
            ),
        )

        // Marca A a 0.0125 por hoja: 300 hojas -> 3.75 el rollo, 15.00 el paquete de 4.
        // La marca B ofrece ese mismo contenido por 14.00, así que es la más barata.
        assertEquals(3.75, marcaAAjustada.precioPorRolloSimulado, delta)
        assertEquals(15.0, marcaAAjustada.precioPaqueteSimulado, delta)
        assertTrue(marcaB.precioPorHoja < marcaA.precioPorHoja)
    }

    @Test
    fun `simular devuelve null con hojas hipoteticas no positivas`() {
        assertNull(
            PaperPriceCalculator.simular(precioPorHoja = 0.01, hojasHipoteticas = 0, rollosPorPaquete = 4),
        )
        assertNull(
            PaperPriceCalculator.simular(precioPorHoja = 0.01, hojasHipoteticas = -10, rollosPorPaquete = 4),
        )
    }

    @Test
    fun `simular devuelve null sin rollos o con precio por hoja invalido`() {
        assertNull(
            PaperPriceCalculator.simular(precioPorHoja = 0.01, hojasHipoteticas = 300, rollosPorPaquete = 0),
        )
        assertNull(
            PaperPriceCalculator.simular(precioPorHoja = -0.01, hojasHipoteticas = 300, rollosPorPaquete = 4),
        )
        assertNull(
            PaperPriceCalculator.simular(
                precioPorHoja = Double.NaN,
                hojasHipoteticas = 300,
                rollosPorPaquete = 4,
            ),
        )
    }
}
