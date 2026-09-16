package com.rolloapp.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rolloapp.app.ui.theme.Spacing
import com.rolloapp.app.ui.theme.Teal
import com.rolloapp.app.ui.theme.TealOscuro
import java.io.File

/**
 * Encabezado de la referencia: un bloque teal que llega hasta el borde superior
 * de la pantalla, con las esquinas de abajo redondeadas, el título y —debajo— el
 * dato que resume la pantalla.
 *
 * Es teal en tema claro y oscuro por igual: es el color de marca, no una
 * superficie más. Por eso el contenido va siempre en blanco y no en `onSurface`.
 */
@Composable
fun PanelSuperior(
    titulo: String,
    modifier: Modifier = Modifier,
    onVolver: (() -> Unit)? = null,
    contenido: @Composable (ColumnScope.() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Teal,
                shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
            )
            .statusBarsPadding()
            .padding(
                start = if (onVolver == null) Spacing.lg else Spacing.xs,
                end = Spacing.lg,
                top = Spacing.sm,
                bottom = Spacing.lg,
            ),
    ) {
        CompositionLocalProvider(LocalContentColor provides Color.White) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (onVolver != null) {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White,
                        )
                    }
                }
                Text(text = titulo, style = MaterialTheme.typography.titleLarge)
            }
            if (contenido != null) {
                Column(
                    modifier = Modifier.padding(
                        start = if (onVolver == null) 0.dp else Spacing.md,
                        top = Spacing.md,
                    ),
                    content = contenido,
                )
            }
        }
    }
}

/** Etiqueta chica sobre el panel teal (va en blanco atenuado, no en gris). */
@Composable
fun EtiquetaPanel(texto: String, modifier: Modifier = Modifier) {
    Text(
        text = texto,
        style = MaterialTheme.typography.bodyMedium,
        color = Color.White.copy(alpha = 0.72f),
        modifier = modifier,
    )
}

/** Cifra grande del panel teal. */
@Composable
fun CifraPanel(texto: String, modifier: Modifier = Modifier) {
    Text(
        text = texto,
        style = MaterialTheme.typography.displaySmall,
        color = Color.White,
        modifier = modifier,
    )
}

/**
 * Cuadrado redondeado con un ícono adentro, como el que acompaña a cada fila de
 * la referencia. Sirve para que la lista se lea de un vistazo sin depender solo
 * del texto.
 */
@Composable
fun ChipIcono(
    icono: ImageVector,
    modifier: Modifier = Modifier,
    fondo: Color = MaterialTheme.colorScheme.primaryContainer,
    contenido: Color = MaterialTheme.colorScheme.onPrimaryContainer,
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .background(color = fondo, shape = RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = contenido,
            modifier = Modifier.size(20.dp),
        )
    }
}

/**
 * Miniatura cuadrada de la foto de un paquete: la imagen si `fotoPath` no es
 * nulo, o un ícono de cámara si todavía no hay foto.
 *
 * Se usa para elegir/retomar la foto en "Agregar paquete" (con `onClick`, ahí
 * lleva un overlay que indica que es tocable), para mostrarla de solo lectura
 * en "Comparar" (sin `onClick`), y para hacer zoom en el detalle (con
 * `onClick` pero `mostrarAccionCamara = false`, porque desde ahí no se puede
 * retomar la foto). El tamaño lo decide quien la usa vía `modifier.size(...)`.
 */
@Composable
fun MiniaturaFoto(
    fotoPath: String?,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    mostrarAccionCamara: Boolean = onClick != null,
) {
    val forma = RoundedCornerShape(12.dp)
    Box(
        modifier = modifier
            .clip(forma)
            .background(color = MaterialTheme.colorScheme.primaryContainer, shape = forma)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        if (fotoPath != null) {
            AsyncImage(
                model = File(fotoPath),
                contentDescription = "Foto del paquete",
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize(),
            )
            if (mostrarAccionCamara) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PhotoCamera,
                        contentDescription = "Volver a tomar la foto",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        } else {
            Icon(
                imageVector = Icons.Outlined.PhotoCamera,
                contentDescription = if (onClick != null) "Tomar foto" else null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

/** Píldora pálida con texto: "Mejor precio", "Simulado". */
@Composable
fun Badge(
    texto: String,
    modifier: Modifier = Modifier,
    fondo: Color = MaterialTheme.colorScheme.primaryContainer,
    contenido: Color = MaterialTheme.colorScheme.onPrimaryContainer,
) {
    Surface(
        modifier = modifier,
        color = fondo,
        contentColor = contenido,
        shape = CircleShape,
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
        )
    }
}

/** Título de sección con una acción opcional a la derecha ("Ver todo"). */
@Composable
fun TituloSeccion(
    texto: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = texto, style = MaterialTheme.typography.titleMedium)
    }
}

/** Color del panel teal, para quien necesite igualarlo (ej. la barra de estado). */
val ColorPanel: Color get() = Teal

/** Variante oscura del teal, para estados presionados. */
val ColorPanelOscuro: Color get() = TealOscuro
