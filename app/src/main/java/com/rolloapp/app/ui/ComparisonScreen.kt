package com.rolloapp.app.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rolloapp.app.ui.theme.Spacing

/**
 * Historial ordenado por precio por hoja ascendente: la primera fila es siempre
 * la opción más conveniente y lleva el badge "Mejor precio".
 *
 * Cada fila muestra el desglose completo (precio por rollo, por hoja y por 100
 * hojas), igual que la versión original — solo con menos padding para que
 * entren varias tarjetas por pantalla sin perder ningún dato.
 */
@Composable
fun ComparisonScreen(
    entradas: List<PaperEntryUi>,
    onSeleccionar: (PaperEntryUi) -> Unit,
    onEliminar: (PaperEntryUi) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (entradas.isEmpty()) {
        EmptyState(modifier = modifier.fillMaxSize())
        return
    }

    // Borrar es destructivo y no hay deshacer, así que siempre pasa por un
    // diálogo. El estado vive acá y no en cada fila: una fila puede salir de
    // composición (scroll, reordenamiento) justo cuando el diálogo está abierto.
    var entradaAEliminar by remember { mutableStateOf<PaperEntryUi?>(null) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = Spacing.md, vertical = Spacing.sm),
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
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { entradaAEliminar = null }) {
                    Text("Cancelar")
                }
            },
        )
    }
}

/**
 * Fila con el desglose completo: marca + badges arriba, composición del
 * paquete debajo, y las 3 métricas unitarias (precio por rollo, por hoja y
 * por 100 hojas) — la misma información que la primera versión, con padding
 * más ajustado para que quepan varias tarjetas por pantalla.
 */
@Composable
private fun EntryCard(
    entrada: PaperEntryUi,
    esMejorOpcion: Boolean,
    onClick: () -> Unit,
    onEliminarClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = Spacing.md,
                    end = Spacing.xs,
                    top = Spacing.md,
                    bottom = Spacing.md,
                )
                .then(
                    if (esMejorOpcion) {
                        Modifier.semantics {
                            contentDescription = "Mejor precio: ${entrada.marca}"
                        }
                    } else {
                        Modifier
                    },
                ),
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                ) {
                    Text(
                        text = entrada.marca,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    if (esMejorOpcion) {
                        Etiqueta(
                            texto = "Mejor precio",
                            color = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                    if (entrada.esSimulado) {
                        Etiqueta(
                            texto = "Simulado",
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        )
                    }
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

                HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))

                MetricRow(
                    etiqueta = "Precio por rollo",
                    valor = formatoMoneda(entrada.precioPorRollo),
                )
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
                MetricRow(
                    etiqueta = "Precio por 100 hojas",
                    valor = formatoMoneda(entrada.precioPor100Hojas),
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
    }
}

/** Badge chico y legible: el rol semántico de M3 puesto tal cual, con texto. */
@Composable
private fun Etiqueta(
    texto: String,
    color: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = color,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.small,
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            modifier = Modifier.padding(horizontal = Spacing.sm, vertical = 2.dp),
        )
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
