package com.rolloapp.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rolloapp.app.domain.PaperPriceCalculator
import com.rolloapp.app.ui.theme.Spacing

/**
 * Detalle de una entrada y simulador: cuánto costaría el mismo paquete si cada
 * rollo trajera otra cantidad de hojas, para compararlo de igual a igual contra
 * otra marca.
 */
@Composable
fun EntryDetailScreen(
    entrada: PaperEntryUi?,
    onVolver: () -> Unit,
    onGuardarSimulacion: (entrada: PaperEntryUi, hojasHipoteticas: Int, precioSimulado: Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (entrada == null) {
        Column(modifier = modifier.fillMaxSize()) {
            PanelSuperior(titulo = "Detalle", onVolver = onVolver)
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Esta entrada ya no existe.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
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
            .verticalScroll(rememberScrollState()),
    ) {
        PanelSuperior(titulo = "Detalle", onVolver = onVolver) {
            Text(
                text = entrada.marca,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(Spacing.md))
            EtiquetaPanel("Precio por hoja")
            Spacer(Modifier.height(Spacing.xs))
            CifraPanel(formatoMoneda(entrada.precioPorHoja))
        }

        Column(
            modifier = Modifier.padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            TarjetaSuave {
                Column(
                    modifier = Modifier.padding(Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    if (entrada.fotoPath != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                        ) {
                            MiniaturaFoto(fotoPath = entrada.fotoPath, modifier = Modifier.size(64.dp))
                            TituloSeccion("Paquete", modifier = Modifier.weight(1f))
                        }
                    } else {
                        TituloSeccion("Paquete")
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    MetricRow("Precio del paquete", formatoMonedaCorta(entrada.precio))
                    MetricRow("Rollos por paquete", entrada.rollosPorPaquete.toString())
                    MetricRow("Hojas por rollo", entrada.hojasPorRollo.toString())
                    MetricRow("Hojas totales", entrada.totalHojas.toString())

                    Spacer(Modifier.height(Spacing.xs))
                    TituloSeccion("Precios unitarios")
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    MetricRow("Precio por rollo", formatoMoneda(entrada.precioPorRollo))
                    MetricRow(
                        etiqueta = "Precio por hoja",
                        valor = formatoMoneda(entrada.precioPorHoja),
                        destacado = true,
                        valorColor = MaterialTheme.colorScheme.primary,
                    )
                    MetricRow("Precio por 100 hojas", formatoMoneda(entrada.precioPor100Hojas))
                }
            }

            TituloSeccion("Simulador")
            Text(
                text = "¿Cuánto costaría este mismo paquete si cada rollo trajera otra " +
                    "cantidad de hojas? Sirve para compararlo de igual a igual contra otra marca.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            CampoFormulario(
                value = hojasTexto,
                onValueChange = { hojasTexto = it },
                label = "Hojas hipotéticas por rollo",
                isError = hojasTexto.isNotBlank() && simulacion == null,
                supportingText = if (hojasTexto.isNotBlank() && simulacion == null) {
                    "Ingresá un número mayor a cero"
                } else {
                    null
                },
                keyboardType = KeyboardType.Number,
            )

            TarjetaSuave {
                Column(
                    modifier = Modifier.padding(Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    if (simulacion == null) {
                        Text(
                            text = "Ingresá una cantidad de hojas para ver la simulación.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    } else {
                        TituloSeccion("Con ${simulacion.hojasHipoteticas} hojas por rollo")
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                        MetricRow(
                            etiqueta = "Precio por rollo simulado",
                            valor = formatoMoneda(simulacion.precioPorRolloSimulado),
                        )
                        MetricRow(
                            etiqueta = "Precio del paquete simulado",
                            valor = formatoMonedaCorta(simulacion.precioPaqueteSimulado),
                            destacado = true,
                            valorColor = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = "El precio por hoja no cambia: " +
                                "${formatoMoneda(entrada.precioPorHoja)} por hoja.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            BotonPrincipal(
                texto = "Guardar simulación",
                enabled = simulacion != null,
                onClick = {
                    val resultado = simulacion ?: return@BotonPrincipal
                    onGuardarSimulacion(
                        entrada,
                        resultado.hojasHipoteticas,
                        resultado.precioPaqueteSimulado,
                    )
                    hojasTexto = ""
                },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(Spacing.sm))
        }
    }
}
