package com.rolloapp.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Esquinas generosas, como en la referencia: tarjetas y campos bien redondeados,
 * y botones en forma de píldora (eso último lo resuelve cada botón con
 * `CircleShape`, no la escala de shapes).
 */
val RolloShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

/**
 * Se declaran todos los roles: los que no se pasan los rellena Material Baseline
 * con su violeta, y reaparecen por la puerta de atrás en diálogos, snackbars y
 * fondos de tarjeta.
 */
private val EsquemaClaro = lightColorScheme(
    primary = Teal,
    onPrimary = Color.White,
    primaryContainer = Menta,
    onPrimaryContainer = TealOscuro,
    inversePrimary = TealClaro,

    secondary = Teal,
    onSecondary = Color.White,
    secondaryContainer = Menta,
    onSecondaryContainer = TealOscuro,

    tertiary = Teal,
    onTertiary = Color.White,
    tertiaryContainer = Menta,
    onTertiaryContainer = TealOscuro,

    error = RojoClaro,
    onError = Color.White,
    errorContainer = RojoContenedorClaro,
    onErrorContainer = RojoClaro,

    background = FondoClaro,
    onBackground = TextoClaro,
    surface = SuperficieClaro,
    onSurface = TextoClaro,
    surfaceVariant = SuperficieTenueClaro,
    onSurfaceVariant = TextoTenueClaro,
    surfaceTint = Color.Transparent,
    surfaceDim = FondoClaro,
    surfaceBright = SuperficieClaro,
    surfaceContainerLowest = SuperficieClaro,
    surfaceContainerLow = SuperficieClaro,
    surfaceContainer = SuperficieAltaClaro,
    surfaceContainerHigh = SuperficieAltaClaro,
    surfaceContainerHighest = SuperficieTenueClaro,

    inverseSurface = TextoClaro,
    inverseOnSurface = Color.White,

    outline = BordeClaro,
    outlineVariant = BordeClaro,
    scrim = Color.Black,
)

private val EsquemaOscuro = darkColorScheme(
    primary = TealClaro,
    onPrimary = Color(0xFF00322F),
    primaryContainer = MentaProfunda,
    onPrimaryContainer = TealClaro,
    inversePrimary = Teal,

    secondary = TealClaro,
    onSecondary = Color(0xFF00322F),
    secondaryContainer = MentaProfunda,
    onSecondaryContainer = TealClaro,

    tertiary = TealClaro,
    onTertiary = Color(0xFF00322F),
    tertiaryContainer = MentaProfunda,
    onTertiaryContainer = TealClaro,

    error = RojoOscuro,
    onError = Color(0xFF3B0A0A),
    errorContainer = RojoContenedorOscuro,
    onErrorContainer = RojoOscuro,

    background = FondoOscuro,
    onBackground = TextoOscuro,
    surface = SuperficieOscuro,
    onSurface = TextoOscuro,
    surfaceVariant = SuperficieTenueOscuro,
    onSurfaceVariant = TextoTenueOscuro,
    surfaceTint = Color.Transparent,
    surfaceDim = FondoOscuro,
    surfaceBright = SuperficieAltaOscuro,
    surfaceContainerLowest = SuperficieTenueOscuro,
    surfaceContainerLow = SuperficieOscuro,
    surfaceContainer = SuperficieOscuro,
    surfaceContainerHigh = SuperficieAltaOscuro,
    surfaceContainerHighest = SuperficieAltaOscuro,

    inverseSurface = TextoOscuro,
    inverseOnSurface = FondoOscuro,

    outline = BordeOscuro,
    outlineVariant = BordeOscuro,
    scrim = Color.Black,
)

/**
 * Sin Material You: la referencia fija un teal concreto como color de marca, y
 * dejar que el wallpaper del usuario lo reemplace haría que la app no se parezca
 * a lo pedido.
 *
 * El panel superior es siempre teal oscuro, en ambos temas, así que los íconos de
 * la barra de estado van en claro — eso se resuelve en `MainActivity`.
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
