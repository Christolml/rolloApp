package com.rolloapp.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rolloapp.app.data.AlmacenFotos
import com.rolloapp.app.domain.PaperPriceBreakdown
import com.rolloapp.app.domain.PaperPriceCalculator
import com.rolloapp.app.ui.theme.Spacing

/**
 * Alta de un paquete. El panel teal de arriba muestra el precio por hoja
 * calculado en vivo: es el número con el que después se ordena "Comparar", así
 * que ocupa el lugar del dato principal de la pantalla.
 */
@Composable
fun AddEntryScreen(
    fotoCapturadaExterna: String?,
    onConsumirFotoCapturada: () -> Unit,
    onAbrirCamara: () -> Unit,
    onGuardar: (
        marca: String,
        precio: Double,
        rollosPorPaquete: Int,
        hojasPorRollo: Int,
        fotoPath: String?,
    ) -> Unit,
    modifier: Modifier = Modifier,
) {
    var marca by rememberSaveable { mutableStateOf("") }
    var precioTexto by rememberSaveable { mutableStateOf("") }
    var rollosTexto by rememberSaveable { mutableStateOf("") }
    var hojasTexto by rememberSaveable { mutableStateOf("") }
    var fotoPath by rememberSaveable { mutableStateOf<String?>(null) }

    // La foto se saca en su propia pantalla (CameraCaptureScreen) y vuelve por acá
    // vía el savedStateHandle de la pantalla anterior en el back stack. Si ya
    // había una foto (por ejemplo, "volver a tomar"), se borra la vieja: cada
    // entrada es dueña de un solo archivo a la vez mientras se está armando.
    LaunchedEffect(fotoCapturadaExterna) {
        val nueva = fotoCapturadaExterna ?: return@LaunchedEffect
        fotoPath?.let { AlmacenFotos.borrar(it) }
        fotoPath = nueva
        onConsumirFotoCapturada()
    }

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
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        PanelSuperior(titulo = "Agregar paquete") {
            EtiquetaPanel("Precio por hoja")
            Spacer(Modifier.height(Spacing.xs))
            CifraPanel(desglose?.let { formatoMoneda(it.precioPorHoja) } ?: "—")
            Spacer(Modifier.height(Spacing.md))
            ResumenPanel(desglose)
        }

        Column(
            modifier = Modifier.padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            TituloSeccion("Datos del paquete")

            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MiniaturaFoto(
                    fotoPath = fotoPath,
                    onClick = onAbrirCamara,
                    modifier = Modifier.size(72.dp),
                )
                CampoFormulario(
                    value = marca,
                    onValueChange = { marca = it },
                    label = "Marca",
                    modifier = Modifier.weight(1f),
                )
            }

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

            Spacer(Modifier.height(Spacing.xs))

            BotonPrincipal(
                texto = "Guardar",
                enabled = desglose != null,
                onClick = {
                    if (precio != null && rollos != null && hojas != null && desglose != null) {
                        onGuardar(marca, precio, rollos, hojas, fotoPath)
                        marca = ""
                        precioTexto = ""
                        rollosTexto = ""
                        hojasTexto = ""
                        fotoPath = null // el archivo ya quedó "adoptado" por la entrada guardada
                    }
                },
            )

            Spacer(Modifier.height(Spacing.sm))
        }
    }
}

/** Las otras dos métricas, en dos columnas sobre el panel teal. */
@Composable
private fun ResumenPanel(desglose: PaperPriceBreakdown?, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth()) {
        Column(Modifier.weight(1f)) {
            EtiquetaPanel("Por rollo")
            Text(
                text = desglose?.let { formatoMoneda(it.precioPorRollo) } ?: "—",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
            )
        }
        Column(Modifier.weight(1f)) {
            EtiquetaPanel("Por 100 hojas")
            Text(
                text = desglose?.let { formatoMoneda(it.precioPor100Hojas) } ?: "—",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
            )
        }
    }
}

/** Botón teal en forma de píldora, como los de la referencia. */
@Composable
internal fun BotonPrincipal(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = Spacing.md),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(texto, style = MaterialTheme.typography.labelLarge)
    }
}

/** Tarjeta blanca con esquinas generosas y sombra apenas perceptible. */
@Composable
internal fun TarjetaSuave(
    modifier: Modifier = Modifier,
    contenido: @Composable () -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        contenido()
    }
}

/**
 * Fila etiqueta / valor reutilizada por las tres pantallas: etiqueta en gris a la
 * izquierda, valor alineado a la derecha.
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
            color = valorColor,
            textAlign = TextAlign.End,
        )
    }
}

/** Campo de texto redondeado, sin el borde duro del outlined por defecto. */
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
        shape = MaterialTheme.shapes.small,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
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
