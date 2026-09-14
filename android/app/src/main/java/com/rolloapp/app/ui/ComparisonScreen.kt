package com.rolloapp.app.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rolloapp.app.ui.theme.Spacing

/**
 * Historial ordenado por precio por hoja ascendente. El panel de arriba resume
 * cuál conviene; cada tarjeta trae el desglose completo (por rollo, por hoja y
 * por 100 hojas) con padding ajustado para que entren varias por pantalla.
 */
@Composable
fun ComparisonScreen(
    entradas: List<PaperEntryUi>,
    onSeleccionar: (PaperEntryUi) -> Unit,
    onEliminar: (PaperEntryUi) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Borrar es destructivo y no hay deshacer, así que siempre pasa por un
    // diálogo. El estado vive acá y no en cada fila: una fila puede salir de
    // composición (scroll, reordenamiento) justo cuando el diálogo está abierto.
    var entradaAEliminar by remember { mutableStateOf<PaperEntryUi?>(null) }

    val mejor = entradas.firstOrNull()

    Column(modifier = modifier.fillMaxSize()) {
        PanelSuperior(titulo = "Comparar") {
            if (mejor == null) {
                EtiquetaPanel("Todavía no hay paquetes para comparar")
            } else {
                EtiquetaPanel("Mejor precio por hoja")
                Spacer(Modifier.height(Spacing.xs))
                CifraPanel(formatoMoneda(mejor.precioPorHoja))
                Spacer(Modifier.height(Spacing.xs))
                Text(
                    text = mejor.marca,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.85f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        if (entradas.isEmpty()) {
            EmptyState(modifier = Modifier.fillMaxSize())
            return@Column
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = Spacing.lg,
                end = Spacing.lg,
                top = Spacing.md,
                bottom = Spacing.xl,
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            itemsIndexed(items = entradas, key = { _, entrada -> entrada.id }) { indice, entrada ->
                EntryCard(
                    entrada = entrada,
                    esMejorOpcion = indice == 0,
                    onClick = { onSeleccionar(entrada) },
                    onEliminarClick = { entradaAEliminar = entrada },
                    // Cuando entra un paquete más barato, las filas existentes se
                    // reacomodan en vez de saltar.
                    modifier = Modifier.animateItem(
                        placementSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMedium,
                        ),
                    ),
                )
            }
        }
    }

    entradaAEliminar?.let { objetivo ->
        AlertDialog(
            onDismissRequest = { entradaAEliminar = null },
            title = { Text("¿Eliminar ${objetivo.marca}?") },
            text = { Text("Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onEliminar(objetivo)
                        entradaAEliminar = null
                    },
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { entradaAEliminar = null }) {
                    Text("Cancelar")
                }
            },
            shape = MaterialTheme.shapes.large,
        )
    }
}

/**
 * Tarjeta de un paquete: chip de ícono + marca y badges arriba, la composición
 * del paquete debajo, y el desglose de precios separado por una línea.
 */
@Composable
private fun EntryCard(
    entrada: PaperEntryUi,
    esMejorOpcion: Boolean,
    onClick: () -> Unit,
    onEliminarClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TarjetaSuave(modifier = modifier.clickable(onClick = onClick)) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (entrada.fotoPath != null) {
                    MiniaturaFoto(fotoPath = entrada.fotoPath, modifier = Modifier.size(40.dp))
                } else {
                    ChipIcono(
                        icono = if (entrada.esSimulado) {
                            Icons.Outlined.Edit
                        } else {
                            Icons.Outlined.ShoppingCart
                        },
                    )
                }
                Spacer(Modifier.size(Spacing.md))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    ) {
                        Text(
                            text = entrada.marca,
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false),
                        )
                        if (esMejorOpcion) Badge("Mejor precio")
                        if (entrada.esSimulado) Badge("Simulado")
                    }
                    Text(
                        text = "${formatoMonedaCorta(entrada.precio)} · " +
                            "${entrada.rollosPorPaquete} rollos · " +
                            "${entrada.hojasPorRollo} hojas c/u",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                IconButton(onClick = onEliminarClick) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Eliminar ${entrada.marca}",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = Spacing.xs),
                color = MaterialTheme.colorScheme.outline,
            )

            MetricRow("Precio por rollo", formatoMoneda(entrada.precioPorRollo))
            MetricRow(
                etiqueta = "Precio por hoja",
                valor = formatoMoneda(entrada.precioPorHoja),
                destacado = true,
                valorColor = if (esMejorOpcion) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
            MetricRow("Precio por 100 hojas", formatoMoneda(entrada.precioPor100Hojas))
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.padding(Spacing.xxl), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Text(
                text = "Todavía no hay paquetes",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "Agregá uno desde la pestaña \"Agregar\" para empezar a comparar.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
