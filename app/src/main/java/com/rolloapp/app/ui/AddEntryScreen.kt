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
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.rolloapp.app.domain.PaperPriceBreakdown
import com.rolloapp.app.domain.PaperPriceCalculator
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

        CampoFormulario(
            value = marca,
            onValueChange = { marca = it },
            label = "Marca",
        )

        CampoFormulario(
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
        )

        CampoFormulario(
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
        )

        CampoFormulario(
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
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Guardar")
        }

        Spacer(Modifier.height(Spacing.sm))
    }
}

/**
 * Resultado del cálculo mientras se escribe. El precio por hoja va destacado
 * porque es el mismo dato con el que ordena y compara la pestaña "Comparar".
 * Cuando falta un dato muestra un guion en vez de colapsar, así el panel no salta
 * de alto mientras se tipea.
 */
@Composable
private fun LiveResultCard(desglose: PaperPriceBreakdown?, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
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
                    text = "Precio por hoja",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = desglose?.let { formatoMoneda(it.precioPorHoja) } ?: "—",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (desglose == null) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                )
            }

            HorizontalDivider()

            MetricRow(
                etiqueta = "Precio por rollo",
                valor = desglose?.let { formatoMoneda(it.precioPorRollo) } ?: "—",
            )
            MetricRow(
                etiqueta = "Precio por 100 hojas",
                valor = desglose?.let { formatoMoneda(it.precioPor100Hojas) } ?: "—",
            )

            if (desglose == null) {
                Text(
                    text = "Completá precio, rollos y hojas para ver los precios unitarios.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/** Encabezado de bloque: texto corto, en oración normal, nunca en mayúsculas. */
@Composable
internal fun SectionLabel(texto: String, modifier: Modifier = Modifier) {
    Text(
        text = texto,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier,
    )
}

/**
 * Fila etiqueta / valor reutilizada por las tres pantallas: la etiqueta en gris a
 * la izquierda, el valor alineado a la derecha. `destacado` lo pasa a negrita, sin
 * cambiar de familia tipográfica.
 */
@Composable
internal fun MetricRow(
    etiqueta: String,
    valor: String,
    modifier: Modifier = Modifier,
    destacado: Boolean = false,
    valorColor: Color = MaterialTheme.colorScheme.onSurface,
    etiquetaColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            color = etiquetaColor,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = valor,
            style = if (destacado) {
                MaterialTheme.typography.titleMedium
            } else {
                MaterialTheme.typography.bodyLarge
            },
            fontWeight = if (destacado) FontWeight.Bold else FontWeight.Normal,
            color = valorColor,
            textAlign = TextAlign.End,
        )
    }
}

/**
 * `OutlinedTextField` de Material 3 con los parámetros que repiten las dos
 * pantallas con formulario. Sin personalización de colores ni de forma: el campo
 * se ve como en cualquier otra app Android.
 */
@Composable
internal fun CampoFormulario(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    supportingText: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        isError = isError,
        supportingText = supportingText?.let { { Text(it) } },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
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
