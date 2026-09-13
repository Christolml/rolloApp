package com.rolloapp.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * Esquemas de respaldo. Se declaran **todos** los roles a mano, incluida la rampa
 * `surfaceContainer*`: `lightColorScheme()` / `darkColorScheme()` rellenan con el
 * violeta de Material Baseline cualquier slot que no se les pase, y esos slots
 * entran por la puerta de atrás (el fondo de cada `Card`, el `AlertDialog`, la
 * `NavigationBar`, el `Snackbar`). Dejarlos sin definir es lo que produce esas
 * manchas violetas que no pertenecen a la paleta.
 */
private val EsquemaClaro = lightColorScheme(
    primary = PrimaryClaro,
    onPrimary = OnPrimaryClaro,
    primaryContainer = PrimaryContainerClaro,
    onPrimaryContainer = OnPrimaryContainerClaro,
    inversePrimary = PrimaryOscuro,

    secondary = SecondaryClaro,
    onSecondary = OnSecondaryClaro,
    secondaryContainer = SecondaryContainerClaro,
    onSecondaryContainer = OnSecondaryContainerClaro,

    tertiary = TertiaryClaro,
    onTertiary = OnTertiaryClaro,
    tertiaryContainer = TertiaryContainerClaro,
    onTertiaryContainer = OnTertiaryContainerClaro,

    error = ErrorClaro,
    onError = OnErrorClaro,
    errorContainer = ErrorContainerClaro,
    onErrorContainer = OnErrorContainerClaro,

    background = BackgroundClaro,
    onBackground = OnBackgroundClaro,
    surface = SurfaceClaro,
    onSurface = OnSurfaceClaro,
    surfaceVariant = SurfaceVariantClaro,
    onSurfaceVariant = OnSurfaceVariantClaro,
    surfaceTint = PrimaryClaro,
    surfaceDim = SurfaceDimClaro,
    surfaceBright = SurfaceBrightClaro,
    surfaceContainerLowest = SurfaceContainerLowestClaro,
    surfaceContainerLow = SurfaceContainerLowClaro,
    surfaceContainer = SurfaceContainerClaro,
    surfaceContainerHigh = SurfaceContainerHighClaro,
    surfaceContainerHighest = SurfaceContainerHighestClaro,

    inverseSurface = InverseSurfaceClaro,
    inverseOnSurface = InverseOnSurfaceClaro,

    outline = OutlineClaro,
    outlineVariant = OutlineVariantClaro,
    scrim = Color.Black,
)

private val EsquemaOscuro = darkColorScheme(
    primary = PrimaryOscuro,
    onPrimary = OnPrimaryOscuro,
    primaryContainer = PrimaryContainerOscuro,
    onPrimaryContainer = OnPrimaryContainerOscuro,
    inversePrimary = PrimaryClaro,

    secondary = SecondaryOscuro,
    onSecondary = OnSecondaryOscuro,
    secondaryContainer = SecondaryContainerOscuro,
    onSecondaryContainer = OnSecondaryContainerOscuro,

    tertiary = TertiaryOscuro,
    onTertiary = OnTertiaryOscuro,
    tertiaryContainer = TertiaryContainerOscuro,
    onTertiaryContainer = OnTertiaryContainerOscuro,

    error = ErrorOscuro,
    onError = OnErrorOscuro,
    errorContainer = ErrorContainerOscuro,
    onErrorContainer = OnErrorContainerOscuro,

    background = BackgroundOscuro,
    onBackground = OnBackgroundOscuro,
    surface = SurfaceOscuro,
    onSurface = OnSurfaceOscuro,
    surfaceVariant = SurfaceVariantOscuro,
    onSurfaceVariant = OnSurfaceVariantOscuro,
    surfaceTint = PrimaryOscuro,
    surfaceDim = SurfaceDimOscuro,
    surfaceBright = SurfaceBrightOscuro,
    surfaceContainerLowest = SurfaceContainerLowestOscuro,
    surfaceContainerLow = SurfaceContainerLowOscuro,
    surfaceContainer = SurfaceContainerOscuro,
    surfaceContainerHigh = SurfaceContainerHighOscuro,
    surfaceContainerHighest = SurfaceContainerHighestOscuro,

    inverseSurface = InverseSurfaceOscuro,
    inverseOnSurface = InverseOnSurfaceOscuro,

    outline = OutlineOscuro,
    outlineVariant = OutlineVariantOscuro,
    scrim = Color.Black,
)

/**
 * Material You por default: en Android 12+ el sistema arma la paleta a partir del
 * wallpaper del usuario, así que la app se ve integrada con el resto del teléfono
 * sin que ninguna elección de color hecha a mano pueda desentonar.
 *
 * En Android 11 o anterior —o si se pasa `dynamicColor = false`, útil para
 * previews— se usan los esquemas teal de [Color.kt].
 *
 * `shapes` no se pasa: las esquinas redondeadas del default de Material 3
 * (4/8/12/16/28dp) son exactamente las que se buscan.
 */
@Composable
fun RolloAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> EsquemaOscuro
        else -> EsquemaClaro
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = RolloTypography,
        content = content,
    )
}
