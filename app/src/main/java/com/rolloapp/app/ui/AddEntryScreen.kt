package com.rolloapp.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.rolloapp.app.domain.PaperPriceBreakdown
import com.rolloapp.app.domain.PaperPriceCalculator
import com.rolloapp.app.ui.theme.JetBrainsMono
import com.rolloapp.app.ui.theme.RolloNumbers
import com.rolloapp.app.ui.theme.Spacing

/**
 * Formulario para registrar un paquete. Recalcula los precios unitarios en vivo
 * mientras el usuario escribe.
 */
@Composable
fun AddEntryScreen(
    onGuardar: (marca: String, precio: Double, rollosPorPaquete: Int, hojasPorRollo: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var marca by rememberSaveable { mutableStateOf("") }
    var precioTexto by rememberSaveable { mutableStateOf("") }
    var rollosTexto by rememberSaveable { mutableStateOf("") }
    var hojasTexto by rememberSaveable { mutableStateOf("") }

    val precio = precioTexto.aDoubleOrNull()
    val rollos = rollosTexto.trim().toIntOrNull()
    val hojas = hojasTexto.trim().toIntOrNull()

    val desglose = remember(precio, rollos, hojas) {
        if (precio != null && rollos != null && hojas != null) {
            PaperPriceCalculator.calcular(precio, rollos, hojas)
        } else {
            null
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        SectionLabel("Datos del paquete")

        InstrumentField(
            value = marca,
            onValueChange = { marca = it },
            label = "Marca",
        )

        InstrumentField(
            value = precioTexto,
            onValueChange = { precioTexto = it },
            label = "Precio del paquete",
            isError = precioTexto.isNotBlank() && precio == null,
            supportingText = if (precioTexto.isNotBlank() && precio == null) {
                "Ingresá un número válido"
            } else {
                null
            },
            keyboardType = KeyboardType.Decimal,
            monoValue = true,
        )

        InstrumentField(
            value = rollosTexto,
            onValueChange = { rollosTexto = it },
            label = "Rollos por paquete",
            isError = rollosTexto.isNotBlank() && (rollos == null || rollos <= 0),
            supportingText = if (rollosTexto.isNotBlank() && (rollos == null || rollos <= 0)) {
                "Ingresá un número mayor a cero"
            } else {
                null
            },
            keyboardType = KeyboardType.Number,
            monoValue = true,
        )

        InstrumentField(
            value = hojasTexto,
            onValueChange = { hojasTexto = it },
            label = "Hojas por rollo",
            isError = hojasTexto.isNotBlank() && (hojas == null || hojas <= 0),
            supportingText = if (hojasTexto.isNotBlank() && (hojas == null || hojas <= 0)) {
                "Ingresá un número mayor a cero"
            } else {
                null
            },
            keyboardType = KeyboardType.Number,
            monoValue = true,
        )

        LiveResultCard(desglose = desglose)

        Button(
            onClick = {
                if (precio != null && rollos != null && hojas != null && desglose != null) {
                    onGuardar(marca, precio, rollos, hojas)
                    marca = ""
                    precioTexto = ""
                    rollosTexto = ""
                    hojasTexto = ""
                }
            },
            enabled = desglose != null,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Guardar")
        }

        Spacer(Modifier.height(Spacing.sm))
    }
}

/**
 * Lectura principal del instrumento: el precio por 100 hojas —la magnitud con la
 * que realmente se comparan dos marcas— en grande, y el resto como métricas de
 * apoyo. Cuando falta un dato muestra un guion en vez de colapsar, así el panel
 * no salta de alto mientras se tipea.
 */
@Composable
private fun LiveResultCard(desglose: PaperPriceBreakdown?, modifier: Modifier = Modifier) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Column(
            modifier = Modifier.padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            SectionLabel("Cálculo en vivo")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(
                    // Placeholder con forma de lectura ("—.—") y no un guion
                    // suelto: mantiene el alto del panel estable y se lee como
                    // un instrumento sin señal, no como un glitch.
                    text = desglose?.let { formatoMoneda(it.precioPor100Hojas) } ?: "—.—",
                    style = RolloNumbers.hero,
                    color = if (desglose == null) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                )
                Text(
                    text = "por 100 hojas",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = Spacing.xs),
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            if (desglose == null) {
                Text(
                    text = "Completá precio, rollos y hojas para ver los precios unitarios.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                MetricRow("Precio por rollo", formatoMoneda(desglose.precioPorRollo))
                MetricRow("Precio por hoja", formatoMoneda(desglose.precioPorHoja))
            }
        }
    }
}

/** Encabezado de bloque: texto corto, en oración normal, nunca en mayúsculas. */
@Composable
internal fun SectionLabel(texto: String, modifier: Modifier = Modifier) {
    Text(
        text = texto,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier,
    )
}

/**
 * Fila etiqueta / valor reutilizada por las tres pantallas. Es el único punto de
 * control de la gramática visual de la app: el texto va en Space Grotesk a la
 * izquierda, el número en mono alineado a la derecha.
 */
@Composable
internal fun MetricRow(
    etiqueta: String,
    valor: String,
    modifier: Modifier = Modifier,
    destacado: Boolean = false,
    valorColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = valor,
            style = if (destacado) RolloNumbers.emphasized else RolloNumbers.regular,
            color = valorColor,
            textAlign = TextAlign.End,
        )
    }
}

/**
 * Campo del formulario con estética de instrumento: sin caja, sólo la línea de
 * base que se enciende en Fósforo al enfocar. Es un `TextField` de M3 con el
 * contenedor en transparente, así el foco, el IME y el estado de error siguen
 * resueltos por el componente estándar.
 */
@Composable
internal fun InstrumentField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    supportingText: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    monoValue: Boolean = false,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        isError = isError,
        supportingText = supportingText?.let { { Text(it) } },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        textStyle = if (monoValue) {
            LocalTextStyle.current.copy(fontFamily = JetBrainsMono)
        } else {
            LocalTextStyle.current
        },
        shape = RectangleShape,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
            errorIndicatorColor = MaterialTheme.colorScheme.error,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
        ),
        modifier = modifier.fillMaxWidth(),
    )
}

/** Acepta coma o punto como separador decimal. */
internal fun String.aDoubleOrNull(): Double? {
    val limpio = trim().replace(',', '.')
    if (limpio.isEmpty()) return null
    val valor = limpio.toDoubleOrNull() ?: return null
    return if (valor.isFinite() && valor >= 0) valor else null
}
