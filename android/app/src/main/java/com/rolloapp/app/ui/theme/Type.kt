package com.rolloapp.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.rolloapp.app.R

/**
 * Poppins, la geométrica redondeada de la referencia. Cuatro pesos estáticos
 * (uno por archivo), así que no hace falta el eje variable ni gatear por versión
 * de Android: el sistema elige el archivo que corresponde a cada [FontWeight].
 */
val Poppins = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_bold, FontWeight.Bold),
)

private fun poppins(
    weight: FontWeight,
    size: TextUnit,
    lineHeight: TextUnit,
    letterSpacing: TextUnit = 0.sp,
) = TextStyle(
    fontFamily = Poppins,
    fontWeight = weight,
    fontSize = size,
    lineHeight = lineHeight,
    letterSpacing = letterSpacing,
)

/**
 * Poppins es más ancha que Roboto: con el `letterSpacing` de la escala Material
 * el texto se abre de más, así que va en 0 salvo en las etiquetas chicas.
 */
val RolloTypography = Typography(
    displayLarge = poppins(FontWeight.Bold, 48.sp, 56.sp),
    displayMedium = poppins(FontWeight.Bold, 40.sp, 48.sp),
    displaySmall = poppins(FontWeight.Bold, 34.sp, 42.sp),
    headlineLarge = poppins(FontWeight.SemiBold, 30.sp, 38.sp),
    headlineMedium = poppins(FontWeight.SemiBold, 26.sp, 34.sp),
    headlineSmall = poppins(FontWeight.SemiBold, 22.sp, 30.sp),
    titleLarge = poppins(FontWeight.SemiBold, 19.sp, 26.sp),
    titleMedium = poppins(FontWeight.SemiBold, 16.sp, 22.sp),
    titleSmall = poppins(FontWeight.SemiBold, 14.sp, 20.sp),
    bodyLarge = poppins(FontWeight.Normal, 15.sp, 22.sp),
    bodyMedium = poppins(FontWeight.Normal, 13.sp, 19.sp),
    bodySmall = poppins(FontWeight.Normal, 12.sp, 17.sp),
    labelLarge = poppins(FontWeight.Medium, 14.sp, 20.sp),
    labelMedium = poppins(FontWeight.Medium, 12.sp, 16.sp),
    labelSmall = poppins(FontWeight.Medium, 11.sp, 15.sp, letterSpacing = 0.2.sp),
)
