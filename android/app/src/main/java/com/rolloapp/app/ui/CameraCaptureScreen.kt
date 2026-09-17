package com.rolloapp.app.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.rolloapp.app.data.AlmacenFotos
import com.rolloapp.app.ui.theme.Spacing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Captura en vivo hablándole directo al sensor con CameraX, en vez de delegar a
 * la app de cámara del sistema por un intent implícito: eso deja pedir
 * explícitamente la resolución más alta disponible y el modo de máxima calidad,
 * algo que un intent no permite controlar (y que hacía que algunas apps de
 * cámara de fabricante devolvieran fotos de menor calidad que en su propia UI).
 */
@Composable
fun CameraCaptureScreen(
    onFotoCapturada: (String) -> Unit,
    onCancelar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    var permisoConcedido by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED,
        )
    }
    var permisoDenegado by remember { mutableStateOf(false) }
    var capturando by remember { mutableStateOf(false) }

    val lanzadorPermiso = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { concedido ->
        permisoConcedido = concedido
        permisoDenegado = !concedido
    }

    LaunchedEffect(Unit) {
        if (!permisoConcedido) lanzadorPermiso.launch(Manifest.permission.CAMERA)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        if (permisoConcedido) {
            val previewView = remember { PreviewView(context) }
            var cameraProviderRef by remember { mutableStateOf<ProcessCameraProvider?>(null) }
            var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

            DisposableEffect(Unit) {
                onDispose { cameraProviderRef?.unbindAll() }
            }

            LaunchedEffect(Unit) {
                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                cameraProviderFuture.addListener(
                    {
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.surfaceProvider = previewView.surfaceProvider
                        }
                        val captura = ImageCapture.Builder()
                            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                            .setResolutionSelector(
                                ResolutionSelector.Builder()
                                    .setResolutionStrategy(ResolutionStrategy.HIGHEST_AVAILABLE_STRATEGY)
                                    .build(),
                            )
                            .build()
                        runCatching {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview,
                                captura,
                            )
                        }
                        cameraProviderRef = cameraProvider
                        imageCapture = captura
                    },
                    ContextCompat.getMainExecutor(context),
                )
            }

            AndroidView(modifier = Modifier.fillMaxSize(), factory = { previewView })

            IconButton(
                onClick = onCancelar,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(Spacing.md),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Cancelar",
                    tint = Color.White,
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = Spacing.xl),
            ) {
                if (capturando) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    IconButton(
                        onClick = {
                            val captura = imageCapture ?: return@IconButton
                            capturando = true
                            val archivo = AlmacenFotos.crearArchivoNuevo(context)
                            val opciones = ImageCapture.OutputFileOptions.Builder(archivo).build()
                            captura.takePicture(
                                opciones,
                                ContextCompat.getMainExecutor(context),
                                object : ImageCapture.OnImageSavedCallback {
                                    override fun onImageSaved(
                                        outputFileResults: ImageCapture.OutputFileResults,
                                    ) {
                                        scope.launch {
                                            val procesada = withContext(Dispatchers.IO) {
                                                AlmacenFotos.downsamplearEnElLugar(archivo)
                                            }
                                            capturando = false
                                            if (procesada) {
                                                onFotoCapturada(archivo.absolutePath)
                                            } else {
                                                archivo.delete()
                                            }
                                        }
                                    }

                                    override fun onError(exception: ImageCaptureException) {
                                        capturando = false
                                        archivo.delete()
                                    }
                                },
                            )
                        },
                        modifier = Modifier
                            .size(72.dp)
                            .background(Color.White, CircleShape),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PhotoCamera,
                            contentDescription = "Sacar foto",
                            tint = Color.Black,
                            modifier = Modifier.size(36.dp),
                        )
                    }
                }
            }
        } else if (permisoDenegado) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(Spacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Text(
                    text = "Se necesita permiso de cámara para sacar la foto.",
                    color = Color.White,
                )
                BotonPrincipal(texto = "Volver", onClick = onCancelar)
            }
        }
    }
}
