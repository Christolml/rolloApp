package com.rolloapp.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Historial completo ordenado por precio por hoja ascendente. La primera entrada
 * es la más conveniente y se resalta con un badge "Mejor opción".
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

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(items = entradas, key = { it.id }) { entrada ->
            DismissableEntry(
                entrada = entrada,
                esMejorOpcion = entrada.id == entradas.first().id,
                onSeleccionar = { onSeleccionar(entrada) },
                onEliminar = { onEliminar(entrada) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DismissableEntry(
    entrada: PaperEntryUi,
    esMejorOpcion: Boolean,
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
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(12.dp),
                    )
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        },
    ) {
        EntryCard(
            entrada = entrada,
            esMejorOpcion = esMejorOpcion,
            onClick = onSeleccionar,
        )
    }
}

@Composable
private fun EntryCard(
    entrada: PaperEntryUi,
    esMejorOpcion: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = if (esMejorOpcion) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        } else {
            CardDefaults.cardColors()
        },
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = entrada.marca,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f, fill = false),
                )
                if (esMejorOpcion) {
                    Etiqueta(
                        texto = "Mejor opción",
                        fondo = MaterialTheme.colorScheme.primary,
                        contenido = MaterialTheme.colorScheme.onPrimary,
                    )
                }
                if (entrada.esSimulado) {
                    Etiqueta(
                        texto = "Simulado",
                        fondo = MaterialTheme.colorScheme.tertiaryContainer,
                        contenido = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                }
            }

            Text(
                text = "${formatoMonedaCorta(entrada.precio)} · " +
                    "${entrada.rollosPorPaquete} rollos · ${entrada.hojasPorRollo} hojas c/u",
                style = MaterialTheme.typography.bodyMedium,
            )

            MetricRow("Precio por hoja", formatoMoneda(entrada.precioPorHoja), destacado = true)
            MetricRow("Precio por 100 hojas", formatoMoneda(entrada.precioPor100Hojas))
            MetricRow("Precio por rollo", formatoMoneda(entrada.precioPorRollo))
        }
    }
}

@Composable
private fun Etiqueta(
    texto: String,
    fondo: androidx.compose.ui.graphics.Color,
    contenido: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = fondo,
        contentColor = contenido,
        shape = RoundedCornerShape(50),
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.padding(32.dp), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Todavía no hay paquetes",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "Agregá uno desde la pestaña \"Agregar\" para empezar a comparar.",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
