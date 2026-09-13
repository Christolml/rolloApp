package com.rolloapp.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
    SeccionPrincipal(Rutas.AGREGAR, "Agregar", Icons.Filled.Add),
    SeccionPrincipal(Rutas.COMPARAR, "Comparar", Icons.AutoMirrored.Filled.List),
)

@OptIn(ExperimentalMaterial3Api::class)
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
        topBar = {
            // Defaults de M3 sin overrides: así el tinte al scrollear y el
            // comportamiento con color dinámico son los que espera el sistema.
            TopAppBar(
                title = {
                    Text(
                        when (rutaActual) {
                            Rutas.AGREGAR -> "Agregar paquete"
                            Rutas.COMPARAR -> "Comparar"
                            else -> "Detalle"
                        },
                    )
                },
                navigationIcon = {
                    if (!esSeccionPrincipal) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                            )
                        }
                    }
                },
            )
        },
        bottomBar = {
            if (esSeccionPrincipal) {
                NavigationBar {
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
                    onGuardar = { marca, precio, rollos, hojas ->
                        viewModel.guardar(marca, precio, rollos, hojas)
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
                    onGuardarSimulacion = { entrada, hojas, precioSimulado ->
                        viewModel.guardar(
                            marca = "${entrada.marca} (${hojas} hojas)",
                            precio = precioSimulado,
                            rollosPorPaquete = entrada.rollosPorPaquete,
                            hojasPorRollo = hojas,
                            esSimulado = true,
                        )
                        scope.launch {
                            snackbarHostState.showSnackbar("Simulación guardada")
                        }
                    },
                )
            }
        }
    }
}
