package com.rolloapp.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Los dos esquemas declaran **toda** la familia de roles a propósito.
 *
 * `darkColorScheme()` / `lightColorScheme()` rellenan con el violeta de Material
 * Baseline cualquier slot que no se les pase, y esos slots se filtran por la
 * puerta de atrás: `surfaceContainer*` en cada `Card`, `inverse*` en el
 * `Snackbar`. Completarlos es lo que mantiene la identidad en toda la app.
 *
 * Regla simétrica del contenido sobre acento: en oscuro los acentos son Glow
 * (claros) y encima va Grafito; en claro son Ink (oscuros) y encima va blanco.
 */
private val EsquemaOscuro = darkColorScheme(
    primary = FosforoGlow,
    onPrimary = Grafito,
    primaryContainer = FosforoContainerDark,
    onPrimaryContainer = OnFosforoContainerDark,
    inversePrimary = FosforoInk,

    secondary = OnSurfaceVariantDark,
    onSecondary = Grafito,
    secondaryContainer = SurfaceDarkContainerHigh,
    onSecondaryContainer = OnSurfaceDark,

    tertiary = VioletaGlow,
    onTertiary = Grafito,
    tertiaryContainer = VioletaContainerDark,
    onTertiaryContainer = OnVioletaContainerDark,

    error = AlertaGlow,
    onError = Grafito,
    errorContainer = AlertaContainerDark,
    onErrorContainer = OnAlertaContainerDark,

    background = Grafito,
    onBackground = OnSurfaceDark,
    surface = Grafito,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceDarkContainerHigh,
    onSurfaceVariant = OnSurfaceVariantDark,
    surfaceDim = SurfaceDarkDim,
    surfaceBright = SurfaceDarkBright,
    surfaceContainerLowest = SurfaceDarkLowest,
    surfaceContainerLow = SurfaceDarkLow,
    surfaceContainer = SurfaceDarkContainer,
    surfaceContainerHigh = SurfaceDarkContainerHigh,
    surfaceContainerHighest = SurfaceDarkContainerHighest,
    // Sin sombras ni tinte tonal: el tinte iguala a la superficie, así elevar
    // un contenedor no lo tiñe de verde.
    surfaceTint = Grafito,

    inverseSurface = Bruma,
    inverseOnSurface = Grafito,

    outline = LineaDark,
    outlineVariant = LineaDark,
    scrim = Color.Black,
)

private val EsquemaClaro = lightColorScheme(
    primary = FosforoInk,
    onPrimary = Color.White,
    primaryContainer = FosforoContainerLight,
    onPrimaryContainer = OnFosforoContainerLight,
    inversePrimary = FosforoGlow,

    secondary = OnSurfaceVariantLight,
    onSecondary = Color.White,
    secondaryContainer = SurfaceLightContainerHigh,
    onSecondaryContainer = OnSurfaceLight,

    tertiary = VioletaInk,
    onTertiary = Color.White,
    tertiaryContainer = VioletaContainerLight,
    onTertiaryContainer = OnVioletaContainerLight,

    error = AlertaInk,
    onError = Color.White,
    errorContainer = AlertaContainerLight,
    onErrorContainer = OnAlertaContainerLight,

    background = Bruma,
    onBackground = OnSurfaceLight,
    surface = Bruma,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceLightContainerHigh,
    onSurfaceVariant = OnSurfaceVariantLight,
    surfaceDim = SurfaceLightDim,
    surfaceBright = SurfaceLightBright,
    surfaceContainerLowest = SurfaceLightLowest,
    surfaceContainerLow = SurfaceLightLow,
    surfaceContainer = SurfaceLightContainer,
    surfaceContainerHigh = SurfaceLightContainerHigh,
    surfaceContainerHighest = SurfaceLightContainerHighest,
    surfaceTint = Bruma,

    inverseSurface = SurfaceDarkContainerHigh,
    inverseOnSurface = OnSurfaceDark,

    outline = LineaLight,
    outlineVariant = LineaLight,
    scrim = Color.Black,
)

/**
 * Sin color dinámico: la identidad de la app no depende del wallpaper del
 * usuario. El instrumento se ve igual en todos los teléfonos.
 */
@Composable
fun RolloAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) EsquemaOscuro else EsquemaClaro,
        typography = RolloTypography,
        shapes = RolloShapes,
        content = content,
    )
}
