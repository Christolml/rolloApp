package com.rolloapp.app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Paleta "HUD de precisión": instrumento de laboratorio, no plantilla por defecto.
 *
 * Cada acento tiene dos variantes del mismo matiz porque los tonos neón que
 * funcionan sobre Grafito caen a 1.5–3:1 de contraste sobre Bruma:
 *  - `Glow`  → modo oscuro (tono luminoso sobre fondo profundo).
 *  - `Ink`   → modo claro (mismo matiz, oscurecido para llegar a contraste AA).
 *
 * Ningún color se consume directamente desde las pantallas: todo se mapea a los
 * roles nativos de `ColorScheme` en `Theme.kt`, así los componentes M3 estándar
 * (TextField en error, Snackbar, SwipeToDismiss) heredan la identidad sin código extra.
 */

// --- Neutros: fondo base y rampa de superficies -----------------------------

/** Grafito: fondo/superficie base del modo oscuro. */
val Grafito = Color(0xFF10161A)

/** Bruma: fondo/superficie base del modo claro. */
val Bruma = Color(0xFFF2F5F4)

val SurfaceDarkLowest = Color(0xFF0A0F12)
val SurfaceDarkLow = Color(0xFF141B1F)
val SurfaceDarkContainer = Color(0xFF182025)
val SurfaceDarkContainerHigh = Color(0xFF1E272C)
val SurfaceDarkContainerHighest = Color(0xFF242F35)
val SurfaceDarkDim = Color(0xFF0C1215)
val SurfaceDarkBright = Color(0xFF2B363C)
val OnSurfaceDark = Color(0xFFEAF2F0)
val OnSurfaceVariantDark = Color(0xFF9FB0AC)

val SurfaceLightLowest = Color(0xFFFFFFFF)
val SurfaceLightLow = Color(0xFFECEFEE)
val SurfaceLightContainer = Color(0xFFE6E9E8)
val SurfaceLightContainerHigh = Color(0xFFDFE3E1)
val SurfaceLightContainerHighest = Color(0xFFD8DDDB)
val SurfaceLightDim = Color(0xFFD5DAD8)
val SurfaceLightBright = Color(0xFFFFFFFF)
val OnSurfaceLight = Color(0xFF10161A)
val OnSurfaceVariantLight = Color(0xFF4B5A57)

// --- Fósforo: mejor precio real, foco de campos, CTA principal --------------

val FosforoGlow = Color(0xFF00E6A0)
val FosforoInk = Color(0xFF00754F)
val FosforoContainerDark = Color(0xFF00402D)
val OnFosforoContainerDark = Color(0xFF7CFFD4)
val FosforoContainerLight = Color(0xFFC6F2E1)
val OnFosforoContainerLight = Color(0xFF00301F)

// --- Violeta señal: entradas simuladas / hipotéticas ------------------------

val VioletaGlow = Color(0xFF8C7BFF)
val VioletaInk = Color(0xFF5B45D6)
val VioletaContainerDark = Color(0xFF2C245C)
val OnVioletaContainerDark = Color(0xFFDCD5FF)
val VioletaContainerLight = Color(0xFFE1DCFF)
val OnVioletaContainerLight = Color(0xFF1D1152)

// --- Alerta: eliminar / inválido -------------------------------------------

val AlertaGlow = Color(0xFFFF5C5C)
val AlertaInk = Color(0xFFC6302E)
val AlertaContainerDark = Color(0xFF541A1A)
val OnAlertaContainerDark = Color(0xFFFFD6D4)
val AlertaContainerLight = Color(0xFFFBDAD8)
val OnAlertaContainerLight = Color(0xFF450F0E)

// --- Línea: divisores y bordes; reemplazan a las sombras --------------------

/**
 * Un solo valor por tema para `outline` y `outlineVariant`: acá la línea *es* el
 * material que define cada contenedor (no hay sombras ni relleno que los separe),
 * así que un `outlineVariant` más tenue que `outline` dejaría las cards
 * prácticamente sin borde sobre el fondo.
 */
val LineaDark = Color(0xFF3A464B)
val LineaLight = Color(0xFFC7D0CE)
