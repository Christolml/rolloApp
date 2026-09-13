package com.rolloapp.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp

/**
 * Escala de magnitud: dónde cae [value] dentro del rango [min]..[max] del
 * historial visible.
 *
 * Es la pieza que convierte al ranking en una lectura y no en una simple lista
 * numerada: dos entradas seguidas pueden estar pegadísimas o a un mundo de
 * distancia, y el punto sobre la línea lo muestra sin obligar a restar precios
 * mentalmente.
 */
@Composable
fun MagnitudeScale(
    value: Double,
    min: Double,
    max: Double,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false,
) {
    val lineColor = MaterialTheme.colorScheme.outlineVariant
    val dotColor = if (highlighted) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    val fraction = remember(value, min, max) {
        if (max <= min) 0.5f else ((value - min) / (max - min)).toFloat().coerceIn(0f, 1f)
    }

    Canvas(modifier = modifier.fillMaxWidth().height(16.dp)) {
        val radius = 3.dp.toPx()
        val strokeWidth = 1.dp.toPx()
        val y = size.height / 2f
        drawLine(lineColor, Offset(radius, y), Offset(size.width - radius, y), strokeWidth)
        val x = radius + fraction * (size.width - 2 * radius)
        drawCircle(dotColor, radius, Offset(x, y))
    }
}
