package com.rolloapp.app.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch

object Rutas {
    const val AGREGAR = "agregar"
    const val COMPARAR = "comparar"
    const val ARG_ENTRY_ID = "entryId"
    const val DETALLE = "detalle/{$ARG_ENTRY_ID}"

    fun detalle(id: Long): String = "detalle/$id"
}

private data class SeccionPrincipal(
    val ruta: String,
    val titulo: String,
    val icono: ImageVector,
)

private val secciones = listOf(
    SeccionPrincipal(Rutas.AGREGAR, "Agregar", Icons.Outlined.Add),
    SeccionPrincipal(Rutas.COMPARAR, "Comparar", Icons.AutoMirrored.Outlined.List),
)

@Composable
fun RolloNavHost(
    viewModel: PaperViewModel,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route
    val esSeccionPrincipal = secciones.any { it.ruta == rutaActual }

    val entradas by viewModel.entradas.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        // Sin `topBar`: cada pantalla dibuja su propio panel teal, que llega hasta
        // el borde superior. Por eso el Scaffold solo reserva la barra de
        // navegación del sistema, no la de estado.
        contentWindowInsets = WindowInsets.navigationBars,
        bottomBar = {
            if (esSeccionPrincipal) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    secciones.forEach { seccion ->
                        NavigationBarItem(
                            selected = rutaActual == seccion.ruta,
                            onClick = {
                                if (rutaActual != seccion.ruta) {
                                    navController.navigate(seccion.ruta) {
                                        popUpTo(Rutas.AGREGAR) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            icon = { Icon(seccion.icono, contentDescription = seccion.titulo) },
                            label = { Text(seccion.titulo) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        )
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Rutas.AGREGAR,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Rutas.AGREGAR) {
                AddEntryScreen(
                    onGuardar = { marca, precio, rollos, hojas, fotoPath ->
                        viewModel.guardar(marca, precio, rollos, hojas, fotoPath = fotoPath)
                        scope.launch {
                            snackbarHostState.showSnackbar("Paquete guardado")
                        }
                    },
                )
            }

            composable(Rutas.COMPARAR) {
                ComparisonScreen(
                    entradas = entradas,
                    onSeleccionar = { navController.navigate(Rutas.detalle(it.id)) },
                    onEliminar = { entrada ->
                        viewModel.eliminar(entrada)
                        scope.launch {
                            snackbarHostState.showSnackbar("${entrada.marca} eliminado")
                        }
                    },
                )
            }

            composable(
                route = Rutas.DETALLE,
                arguments = listOf(navArgument(Rutas.ARG_ENTRY_ID) { type = NavType.LongType }),
            ) { entry ->
                val entryId = entry.arguments?.getLong(Rutas.ARG_ENTRY_ID) ?: -1L
                EntryDetailScreen(
                    entrada = entradas.firstOrNull { it.id == entryId },
                    onVolver = { navController.popBackStack() },
                    onGuardarSimulacion = { entrada, hojas, precioSimulado ->
                        viewModel.guardarSimulacion(entrada, hojas, precioSimulado)
                        scope.launch {
                            snackbarHostState.showSnackbar("Simulación guardada")
                        }
                    },
                )
            }
        }
    }
}
