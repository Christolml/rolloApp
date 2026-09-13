package com.rolloapp.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.rolloapp.app.domain.PaperPriceCalculator

/**
 * Detalle de una entrada del historial y simulador: permite ver cuánto costaría
 * un rollo (y el paquete completo) si trajera otra cantidad de hojas, para poder
 * compararlo de forma justa contra otra marca.
 */
@Composable
fun EntryDetailScreen(
    entrada: PaperEntryUi?,
    onGuardarSimulacion: (entrada: PaperEntryUi, hojasHipoteticas: Int, precioSimulado: Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (entrada == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "Esta entrada ya no existe.",
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        return
    }

    var hojasTexto by rememberSaveable(entrada.id) { mutableStateOf("") }
    val hojasHipoteticas = hojasTexto.trim().toIntOrNull()

    val simulacion = remember(entrada.precioPorHoja, hojasHipoteticas, entrada.rollosPorPaquete) {
        hojasHipoteticas?.let {
            PaperPriceCalculator.simular(
                precioPorHoja = entrada.precioPorHoja,
                hojasHipoteticas = it,
                rollosPorPaquete = entrada.rollosPorPaquete,
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = entrada.marca,
            style = MaterialTheme.typography.headlineSmall,
        )
        if (entrada.esSimulado) {
            Text(
                text = "Entrada simulada",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.tertiary,
            )
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text("Paquete", style = MaterialTheme.typography.titleSmall)
                HorizontalDivider()
                MetricRow("Precio del paquete", formatoMonedaCorta(entrada.precio))
                MetricRow("Rollos por paquete", entrada.rollosPorPaquete.toString())
                MetricRow("Hojas por rollo", entrada.hojasPorRollo.toString())
                MetricRow("Hojas totales", entrada.totalHojas.toString())

                Spacer(Modifier.height(4.dp))
                Text("Precios unitarios", style = MaterialTheme.typography.titleSmall)
                HorizontalDivider()
                MetricRow("Precio por rollo", formatoMoneda(entrada.precioPorRollo))
                MetricRow("Precio por hoja", formatoMoneda(entrada.precioPorHoja), destacado = true)
                MetricRow("Precio por 100 hojas", formatoMoneda(entrada.precioPor100Hojas))
            }
        }

        Text(
            text = "Simulador",
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = "¿Cuánto costaría este mismo paquete si cada rollo trajera otra " +
                "cantidad de hojas? Sirve para compararlo de igual a igual contra otra marca.",
            style = MaterialTheme.typography.bodyMedium,
        )

        OutlinedTextField(
            value = hojasTexto,
            onValueChange = { hojasTexto = it },
            label = { Text("Hojas hipotéticas por rollo") },
            singleLine = true,
            isError = hojasTexto.isNotBlank() && simulacion == null,
            supportingText = {
                if (hojasTexto.isNotBlank() && simulacion == null) {
                    Text("Ingresá un número mayor a cero")
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (simulacion == null) {
                    Text(
                        text = "Ingresá una cantidad de hojas para ver la simulación.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                } else {
                    Text(
                        text = "Con ${simulacion.hojasHipoteticas} hojas por rollo",
                        style = MaterialTheme.typography.titleSmall,
                    )
                    HorizontalDivider()
                    MetricRow(
                        etiqueta = "Precio por rollo simulado",
                        valor = formatoMoneda(simulacion.precioPorRolloSimulado),
                    )
                    MetricRow(
                        etiqueta = "Precio del paquete simulado",
                        valor = formatoMonedaCorta(simulacion.precioPaqueteSimulado),
                        destacado = true,
                    )
                    Text(
                        text = "El precio por hoja no cambia: " +
                            "${formatoMoneda(entrada.precioPorHoja)} por hoja.",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }

        Button(
            onClick = {
                val resultado = simulacion ?: return@Button
                onGuardarSimulacion(
                    entrada,
                    resultado.hojasHipoteticas,
                    resultado.precioPaqueteSimulado,
                )
                hojasTexto = ""
            },
            enabled = simulacion != null,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Guardar simulación como nueva entrada")
        }

        Spacer(Modifier.height(8.dp))
    }
}
