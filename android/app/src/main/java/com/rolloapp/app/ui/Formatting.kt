package com.rolloapp.app.ui

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

private val symbols = DecimalFormatSymbols(Locale.US)
private val dosDecimales = DecimalFormat("0.00", symbols)
private val cuatroDecimales = DecimalFormat("0.0000", symbols)
private val seisDecimales = DecimalFormat("0.000000", symbols)

/**
 * Formatea un importe como moneda.
 *
 * Los precios unitarios pequeños se muestran con más decimales, no solo para que
 * no queden en "$0.00": redondear el precio por hoja a 2 decimales hace que las
 * cifras derivadas no cierren contra lo que se ve en pantalla. Con un paquete de
 * $4500 / 16 rollos / 320 hojas, el precio por hoja real es 0.87890625: mostrado
 * como "$0.88" invita a multiplicar por 100 y esperar "$88.00", pero el precio
 * por 100 hojas real es $87.89. Mostrando "$0.8789" las dos cifras cierran.
 */
fun formatoMoneda(valor: Double): String {
    if (!valor.isFinite()) return "—"
    val absoluto = kotlin.math.abs(valor)
    val formateado = when {
        absoluto == 0.0 -> dosDecimales.format(valor)
        absoluto < 0.0001 -> seisDecimales.format(valor)
        absoluto < 1.0 -> cuatroDecimales.format(valor)
        else -> dosDecimales.format(valor)
    }
    return "$$formateado"
}

/** Formatea un importe siempre con dos decimales (precios de paquete y de rollo). */
fun formatoMonedaCorta(valor: Double): String =
    if (valor.isFinite()) "$${dosDecimales.format(valor)}" else "—"
