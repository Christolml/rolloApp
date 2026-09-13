package com.rolloapp.app.ui.theme

import android.os.Build
import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.rolloapp.app.R

/**
 * Dos familias con roles que no se intercambian:
 *  - Space Grotesk → todo lo que es lenguaje (títulos, labels, botones, prosa).
 *  - JetBrains Mono → sólo valores numéricos aislados, vía [RolloNumbers].
 *
 * Ambos archivos son fuentes variables (un solo `.ttf`, eje `wght`), así que los
 * pesos se piden por `FontVariation`. Eso requiere API 26; en 24–25 se carga una
 * única instancia default del archivo, que es una degradación aceptable y evita
 * el crash de aplicar variaciones en un runtime que no las soporta.
 */
@OptIn(ExperimentalTextApi::class)
private fun familiaVariable(resId: Int, pesos: List<Int>): FontFamily =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        FontFamily(
            pesos.map { peso ->
                Font(
                    resId = resId,
                    weight = FontWeight(peso),
                    variationSettings = FontVariation.Settings(FontVariation.weight(peso)),
                )
            },
        )
    } else {
        FontFamily(Font(resId))
    }

val SpaceGrotesk: FontFamily = familiaVariable(
    resId = R.font.space_grotesk_variable,
    pesos = listOf(400, 500, 600, 700),
)

val JetBrainsMono: FontFamily = familiaVariable(
    resId = R.font.jetbrains_mono_variable,
    pesos = listOf(400, 500, 600),
)

/**
 * Los 15 roles de Material 3 en Space Grotesk. Tracking más ajustado que el
 * default (y negativo en los tamaños grandes): un panel de instrumento se lee
 * compacto, no espaciado.
 */
val RolloTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Medium,
        fontSize = 57.sp,
        lineHeight = 62.sp,
        letterSpacing = (-1.0).sp,
    ),
    displayMedium = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Medium,
        fontSize = 45.sp,
        lineHeight = 50.sp,
        letterSpacing = (-0.8).sp,
    ),
    displaySmall = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Medium,
        fontSize = 36.sp,
        lineHeight = 42.sp,
        letterSpacing = (-0.5).sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 38.sp,
        letterSpacing = (-0.5).sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.4).sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.3).sp,
    ),
    titleLarge = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = (-0.2).sp,
    ),
    titleMedium = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.2.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.3.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.3.sp,
    ),
)

/**
 * Estilos monoespaciados para **valores numéricos aislados** (un precio, una
 * cantidad, un índice de ranking). Nunca para oraciones que mencionan un número
 * de paso: esas siguen en Space Grotesk.
 *
 * Al ser monoespaciada, una columna de precios queda alineada dígito a dígito y
 * los valores no “bailan” mientras se tipea en el formulario.
 */
object RolloNumbers {
    /** Número grande del cálculo en vivo. */
    val hero = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Medium,
        fontSize = 40.sp,
        lineHeight = 46.sp,
        letterSpacing = (-1.5).sp,
    )

    /** Valor destacado dentro de una lista de métricas. */
    val emphasized = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = (-0.5).sp,
    )

    /** Valor normal de una fila de métrica. */
    val regular = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = (-0.2).sp,
    )

    /** Dato secundario en letra chica. */
    val caption = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp,
    )

    /** Marcador de posición del ranking: "01", "02", "03". */
    val rankIndex = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 1.sp,
    )
}
