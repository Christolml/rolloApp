package com.rolloapp.app.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rolloapp.app.ui.theme.RolloNumbers
import com.rolloapp.app.ui.theme.Spacing

/**
 * Ranking real: el historial ya llega ordenado por precio por hoja ascendente,
 * así que la posición 01 es la opción más conveniente y los marcadores numéricos
 * están justificados. La franja Fósforo del borde izquierdo marca esa primera
 * posición sin teñir la card entera.
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

    // Los extremos del rango se calculan una sola vez para toda la lista, no por
    // fila: la escala tiene que ser la misma para que comparar tenga sentido.
    val minPrecioPorHoja = remember(entradas) { entradas.minOfOrNull { it.precioPorHoja } ?: 0.0 }
    val maxPrecioPorHoja = remember(entradas) { entradas.maxOfOrNull { it.precioPorHoja } ?: 0.0 }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        itemsIndexed(items = entradas, key = { _, entrada -> entrada.id }) { indice, entrada ->
            DismissableEntry(
                entrada = entrada,
                posicion = indice + 1,
                esMejorOpcion = indice == 0,
                minPrecioPorHoja = minPrecioPorHoja,
                maxPrecioPorHoja = maxPrecioPorHoja,
                onSeleccionar = { onSeleccionar(entrada) },
                onEliminar = { onEliminar(entrada) },
                // Única animación de la app: cuando entra un paquete más barato,
                // las filas existentes se reacomodan en vez de saltar.
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DismissableEntry(
    entrada: PaperEntryUi,
    posicion: Int,
    esMejorOpcion: Boolean,
    minPrecioPorHoja: Double,
    maxPrecioPorHoja: Double,
    onSeleccionar: () -> Unit,
    onEliminar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { valor ->
            if (valor == SwipeToDismissBoxValue.StartToEnd || valor == SwipeToDismissBoxValue.EndToStart) {
                onEliminar()
                true
            } else {
                false
            }
        },
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.error,
                        // Mismo shape que la card de contenido: si difieren se ve
                        // una costura durante el gesto.
                        shape = MaterialTheme.shapes.medium,
                    )
                    .padding(horizontal = Spacing.xl),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.onError,
                )
            }
        },
    ) {
        EntryCard(
            entrada = entrada,
            posicion = posicion,
            esMejorOpcion = esMejorOpcion,
            minPrecioPorHoja = minPrecioPorHoja,
            maxPrecioPorHoja = maxPrecioPorHoja,
            onClick = onSeleccionar,
        )
    }
}

@Composable
private fun EntryCard(
    entrada: PaperEntryUi,
    posicion: Int,
    esMejorOpcion: Boolean,
    minPrecioPorHoja: Double,
    maxPrecioPorHoja: Double,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val acento = if (esMejorOpcion) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    OutlinedCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .then(
                    if (esMejorOpcion) {
                        Modifier.semantics {
                            contentDescription = "Mejor opción: ${entrada.marca}"
                        }
                    } else {
                        Modifier
                    },
                ),
        ) {
            // La franja reemplaza a la card entera pintada: marca la posición 01
            // sin gritar ni romper la jerarquía de color del resto del contenido.
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight()
                    .background(if (esMejorOpcion) acento else Color.Transparent),
            )

            Column(
                modifier = Modifier.padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = posicion.toString().padStart(2, '0'),
                        style = RolloNumbers.rankIndex,
                        color = acento,
                    )
                    Text(
                        text = entrada.marca,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    if (entrada.esSimulado) {
                        EtiquetaBorde(
                            texto = "Simulado",
                            color = MaterialTheme.colorScheme.tertiary,
                        )
                    }
                }

                MagnitudeScale(
                    value = entrada.precioPorHoja,
                    min = minPrecioPorHoja,
                    max = maxPrecioPorHoja,
                    highlighted = esMejorOpcion,
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
                MetricRow("Precio por 100 hojas", formatoMoneda(entrada.precioPor100Hojas))
                MetricRow("Precio por rollo", formatoMoneda(entrada.precioPorRollo))

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                MetricRow("Precio del paquete", formatoMonedaCorta(entrada.precio))
                MetricRow("Rollos por paquete", entrada.rollosPorPaquete.toString())
                MetricRow("Hojas por rollo", entrada.hojasPorRollo.toString())
            }
        }
    }
}

/**
 * Etiqueta con borde, no píldora rellena: informa una condición de la entrada
 * sin competir en peso visual con los valores numéricos.
 */
@Composable
private fun EtiquetaBorde(
    texto: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = Color.Transparent,
        contentColor = color,
        border = BorderStroke(1.dp, color),
        shape = MaterialTheme.shapes.extraSmall,
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelSmall,
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
