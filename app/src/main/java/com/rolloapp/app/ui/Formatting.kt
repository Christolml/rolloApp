package com.rolloapp.app.ui

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

private val symbols = DecimalFormatSymbols(Locale.US)
private val dosDecimales = DecimalFormat("0.00", symbols)
private val cuatroDecimales = DecimalFormat("0.0000", symbols)
private val seisDecimales = DecimalFormat("0.000000", symbols)

/**
 * Formatea un importe como moneda. El precio por hoja suele ser muy pequeño, así
 * que se amplían los decimales automáticamente para que no se muestre "$0.00".
 */
fun formatoMoneda(valor: Double): String {
    if (!valor.isFinite()) return "—"
    val absoluto = kotlin.math.abs(valor)
    val formateado = when {
        absoluto == 0.0 -> dosDecimales.format(valor)
        absoluto < 0.0001 -> seisDecimales.format(valor)
        absoluto < 0.01 -> cuatroDecimales.format(valor)
        else -> dosDecimales.format(valor)
    }
    return "$$formateado"
}

/** Formatea un importe siempre con dos decimales (precios de paquete y de rollo). */
fun formatoMonedaCorta(valor: Double): String =
    if (valor.isFinite()) "$${dosDecimales.format(valor)}" else "—"
