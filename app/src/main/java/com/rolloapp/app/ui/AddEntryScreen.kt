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
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.rolloapp.app.domain.PaperPriceBreakdown
import com.rolloapp.app.domain.PaperPriceCalculator

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
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Datos del paquete",
            style = MaterialTheme.typography.titleMedium,
        )

        OutlinedTextField(
            value = marca,
            onValueChange = { marca = it },
            label = { Text("Marca") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = precioTexto,
            onValueChange = { precioTexto = it },
            label = { Text("Precio del paquete") },
            singleLine = true,
            isError = precioTexto.isNotBlank() && precio == null,
            supportingText = {
                if (precioTexto.isNotBlank() && precio == null) Text("Ingresá un número válido")
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = rollosTexto,
            onValueChange = { rollosTexto = it },
            label = { Text("Rollos por paquete") },
            singleLine = true,
            isError = rollosTexto.isNotBlank() && (rollos == null || rollos <= 0),
            supportingText = {
                if (rollosTexto.isNotBlank() && (rollos == null || rollos <= 0)) {
                    Text("Ingresá un número mayor a cero")
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = hojasTexto,
            onValueChange = { hojasTexto = it },
            label = { Text("Hojas por rollo") },
            singleLine = true,
            isError = hojasTexto.isNotBlank() && (hojas == null || hojas <= 0),
            supportingText = {
                if (hojasTexto.isNotBlank() && (hojas == null || hojas <= 0)) {
                    Text("Ingresá un número mayor a cero")
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
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

        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun LiveResultCard(desglose: PaperPriceBreakdown?, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Cálculo en vivo",
                style = MaterialTheme.typography.titleSmall,
            )
            HorizontalDivider()

            if (desglose == null) {
                Text(
                    text = "Completá precio, rollos y hojas para ver los precios unitarios.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            } else {
                MetricRow("Precio por rollo", formatoMoneda(desglose.precioPorRollo))
                MetricRow("Precio por hoja", formatoMoneda(desglose.precioPorHoja))
                MetricRow(
                    etiqueta = "Precio por 100 hojas",
                    valor = formatoMoneda(desglose.precioPor100Hojas),
                    destacado = true,
                )
            }
        }
    }
}

/** Fila etiqueta / valor reutilizada por las pantallas de alta y de detalle. */
@Composable
internal fun MetricRow(
    etiqueta: String,
    valor: String,
    modifier: Modifier = Modifier,
    destacado: Boolean = false,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = valor,
            style = if (destacado) {
                MaterialTheme.typography.titleMedium
            } else {
                MaterialTheme.typography.bodyLarge
            },
            fontWeight = if (destacado) FontWeight.Bold else FontWeight.Medium,
        )
    }
}

/** Acepta coma o punto como separador decimal. */
internal fun String.aDoubleOrNull(): Double? {
    val limpio = trim().replace(',', '.')
    if (limpio.isEmpty()) return null
    val valor = limpio.toDoubleOrNull() ?: return null
    return if (valor.isFinite() && valor >= 0) valor else null
}
