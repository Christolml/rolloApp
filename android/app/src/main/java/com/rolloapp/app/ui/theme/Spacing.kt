package com.rolloapp.app.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Escala de espaciado única de la app. Son los mismos seis valores que ya usaban
 * las tres pantallas, pero con nombre: evita que aparezcan literales `dp` sueltos
 * que después se desalinean entre pantallas.
 */
object Spacing {
    val xs: Dp = 4.dp
    val sm: Dp = 8.dp
    val md: Dp = 12.dp
    val lg: Dp = 16.dp
    val xl: Dp = 24.dp
    val xxl: Dp = 32.dp
}
