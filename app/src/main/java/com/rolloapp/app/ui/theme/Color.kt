package com.rolloapp.app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Paleta de respaldo: la que se usa cuando el dispositivo no tiene Material You
 * (Android 11 o anterior) o si se desactiva el color dinámico.
 *
 * Un solo matiz semilla —verde azulado, tono 40 (`#00695C`)— y toda la rampa
 * derivada de él. Es el mismo teal que Material usa desde hace años: se asocia a
 * "ahorro / buen precio" sin ser un acento saturado, y al venir de un único hue
 * no puede desafinar contra sí mismo.
 *
 * `secondary` y `tertiary` son parientes cercanos del mismo eje (verde grisáceo
 * y azul apagado, la rotación de hue que hace el propio Material 3), no acentos
 * que compitan. `error` es el rojo estándar de Material 3.
 *
 * Los nombres siguen la convención `Rol` + `Claro`/`Oscuro` porque cada valor se
 * consume desde un único slot de `ColorScheme` en [RolloAppTheme]; ninguna
 * pantalla importa colores de acá directamente.
 */

// --- Tema claro -------------------------------------------------------------

val PrimaryClaro = Color(0xFF00695C)
val OnPrimaryClaro = Color(0xFFFFFFFF)
val PrimaryContainerClaro = Color(0xFFA7F3E2)
val OnPrimaryContainerClaro = Color(0xFF00201A)

val SecondaryClaro = Color(0xFF4A635C)
val OnSecondaryClaro = Color(0xFFFFFFFF)
val SecondaryContainerClaro = Color(0xFFCCE8DF)
val OnSecondaryContainerClaro = Color(0xFF06201A)

val TertiaryClaro = Color(0xFF3A6470)
val OnTertiaryClaro = Color(0xFFFFFFFF)
val TertiaryContainerClaro = Color(0xFFBEEAF8)
val OnTertiaryContainerClaro = Color(0xFF001F28)

val ErrorClaro = Color(0xFFBA1A1A)
val OnErrorClaro = Color(0xFFFFFFFF)
val ErrorContainerClaro = Color(0xFFFFDAD6)
val OnErrorContainerClaro = Color(0xFF410002)

val BackgroundClaro = Color(0xFFF6FBF8)
val OnBackgroundClaro = Color(0xFF191C1B)
val SurfaceClaro = Color(0xFFF6FBF8)
val OnSurfaceClaro = Color(0xFF191C1B)
val SurfaceVariantClaro = Color(0xFFDBE5E0)
val OnSurfaceVariantClaro = Color(0xFF3F4945)

val OutlineClaro = Color(0xFF6F7975)
val OutlineVariantClaro = Color(0xFFBFC9C4)

val SurfaceDimClaro = Color(0xFFD6DBD8)
val SurfaceBrightClaro = Color(0xFFF6FBF8)
val SurfaceContainerLowestClaro = Color(0xFFFFFFFF)
val SurfaceContainerLowClaro = Color(0xFFF0F5F2)
val SurfaceContainerClaro = Color(0xFFEAEFEC)
val SurfaceContainerHighClaro = Color(0xFFE5EAE7)
val SurfaceContainerHighestClaro = Color(0xFFDFE4E1)

val InverseSurfaceClaro = Color(0xFF2B322F)
val InverseOnSurfaceClaro = Color(0xFFECF2EE)

// --- Tema oscuro ------------------------------------------------------------

val PrimaryOscuro = Color(0xFF5DDBC0)
val OnPrimaryOscuro = Color(0xFF00382D)
val PrimaryContainerOscuro = Color(0xFF005142)
val OnPrimaryContainerOscuro = Color(0xFF7BF8DC)

val SecondaryOscuro = Color(0xFFB1CCC3)
val OnSecondaryOscuro = Color(0xFF1C352F)
val SecondaryContainerOscuro = Color(0xFF334B45)
val OnSecondaryContainerOscuro = Color(0xFFCCE8DF)

val TertiaryOscuro = Color(0xFFA2CDDC)
val OnTertiaryOscuro = Color(0xFF023541)
val TertiaryContainerOscuro = Color(0xFF204C58)
val OnTertiaryContainerOscuro = Color(0xFFBEEAF8)

val ErrorOscuro = Color(0xFFFFB4AB)
val OnErrorOscuro = Color(0xFF690005)
val ErrorContainerOscuro = Color(0xFF93000A)
val OnErrorContainerOscuro = Color(0xFFFFDAD6)

val BackgroundOscuro = Color(0xFF0F1513)
val OnBackgroundOscuro = Color(0xFFDFE4E1)
val SurfaceOscuro = Color(0xFF0F1513)
val OnSurfaceOscuro = Color(0xFFDFE4E1)
val SurfaceVariantOscuro = Color(0xFF3F4945)
val OnSurfaceVariantOscuro = Color(0xFFBFC9C4)

val OutlineOscuro = Color(0xFF899390)
val OutlineVariantOscuro = Color(0xFF3F4945)

val SurfaceDimOscuro = Color(0xFF0F1513)
val SurfaceBrightOscuro = Color(0xFF353B38)
val SurfaceContainerLowestOscuro = Color(0xFF0A0F0E)
val SurfaceContainerLowOscuro = Color(0xFF191C1B)
val SurfaceContainerOscuro = Color(0xFF1D211F)
val SurfaceContainerHighOscuro = Color(0xFF272B29)
val SurfaceContainerHighestOscuro = Color(0xFF323634)

val InverseSurfaceOscuro = Color(0xFFDFE4E1)
val InverseOnSurfaceOscuro = Color(0xFF2B322F)
