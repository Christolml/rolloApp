package com.rolloapp.app.domain

/**
 * Desglose de precios unitarios de un paquete de papel higiénico.
 *
 * Todos los valores están expresados en la misma moneda que el precio del paquete.
 */
data class PaperPriceBreakdown(
    val precioPorRollo: Double,
    val precioPorHoja: Double,
    val precioPor100Hojas: Double,
)

/**
 * Resultado de simular cuánto costaría un rollo (y el paquete completo) del mismo
 * producto si trajera una cantidad distinta de hojas.
 */
data class SimulationResult(
    val hojasHipoteticas: Int,
    val precioPorRolloSimulado: Double,
    val precioPaqueteSimulado: Double,
)

/**
 * Funciones puras de cálculo. No dependen de Android ni de Room, por lo que se
 * pueden probar con JUnit normal (ver `PaperPriceCalculatorTest`).
 */
object PaperPriceCalculator {

    /** `precio / rollosPorPaquete` */
    fun precioPorRollo(precio: Double, rollosPorPaquete: Int): Double =
        precio / rollosPorPaquete

    /** `precioPorRollo / hojasPorRollo` */
    fun precioPorHoja(precioPorRollo: Double, hojasPorRollo: Int): Double =
        precioPorRollo / hojasPorRollo

    /** `precioPorHoja * 100` */
    fun precioPor100Hojas(precioPorHoja: Double): Double =
        precioPorHoja * 100

    /**
     * Calcula el desglose completo de un paquete.
     *
     * @return `null` si alguno de los datos no es utilizable (precio negativo,
     *   cero rollos, cero hojas, o valores no finitos).
     */
    fun calcular(precio: Double, rollosPorPaquete: Int, hojasPorRollo: Int): PaperPriceBreakdown? {
        if (!esEntradaValida(precio, rollosPorPaquete, hojasPorRollo)) return null

        val porRollo = precioPorRollo(precio, rollosPorPaquete)
        val porHoja = precioPorHoja(porRollo, hojasPorRollo)
        return PaperPriceBreakdown(
            precioPorRollo = porRollo,
            precioPorHoja = porHoja,
            precioPor100Hojas = precioPor100Hojas(porHoja),
        )
    }

    /**
     * Simula el costo de un rollo y del paquete completo si el mismo producto
     * trajera [hojasHipoteticas] hojas por rollo, manteniendo el precio por hoja.
     *
     * @param precioPorHoja precio por hoja ya calculado de una entrada existente.
     * @return `null` si los datos no son utilizables.
     */
    fun simular(
        precioPorHoja: Double,
        hojasHipoteticas: Int,
        rollosPorPaquete: Int,
    ): SimulationResult? {
        if (precioPorHoja < 0 || !precioPorHoja.isFinite()) return null
        if (hojasHipoteticas <= 0 || rollosPorPaquete <= 0) return null

        val porRolloSimulado = precioPorHoja * hojasHipoteticas
        return SimulationResult(
            hojasHipoteticas = hojasHipoteticas,
            precioPorRolloSimulado = porRolloSimulado,
            precioPaqueteSimulado = porRolloSimulado * rollosPorPaquete,
        )
    }

    /** Indica si los datos de un paquete permiten calcular precios unitarios. */
    fun esEntradaValida(precio: Double, rollosPorPaquete: Int, hojasPorRollo: Int): Boolean =
        precio >= 0 && precio.isFinite() && rollosPorPaquete > 0 && hojasPorRollo > 0
}
