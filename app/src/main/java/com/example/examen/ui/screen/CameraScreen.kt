package com.example.examen.ui.screen

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.core.content.ContextCompat
import java.io.File
import java.time.LocalDateTime

@Composable
fun CameraScreen(
    lanzadorPermisos: ActivityResultLauncher<Array<String>>,
    navController: NavController,
    onImageCaptured: (Uri) -> Unit
) {
    val contexto = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraController = remember { LifecycleCameraController(contexto) }

    // Lanza la solicitud de permisos para la cámara
    LaunchedEffect(Unit) {
        lanzadorPermisos.launch(arrayOf(Manifest.permission.CAMERA))
    }

    // Configura el controlador de cámara en un efecto compuesto
    LaunchedEffect(cameraController) {
        cameraController.bindToLifecycle(lifecycleOwner)
    }

    // Contenedor principal que ocupa todo el espacio disponible
    Box(modifier = Modifier.fillMaxSize()) {
        // Vista de Android para mostrar la vista previa de la cámara
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                PreviewView(it).apply {
                    controller = cameraController
                }
            }
        )
        // Botón para capturar una foto, alineado en la parte inferior central
        Button(
            onClick = {
                val archivo = crearArchivoImagenPrivado(contexto)
                capturarFoto(cameraController, archivo, contexto) { uri ->
                    onImageCaptured(uri)
                    navController.popBackStack() // Regresa a la pantalla anterior
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text("Capturar Foto", fontSize = 20.sp)
        }
    }
}


// Función para generar un nombre de archivo único basado en la fecha y hora actual hasta el segundo
fun generarNombreSegunFechaHastasegundo(): String = LocalDateTime
    .now().toString().replace(Regex("[-:]"), "").substring(0, 14)

// Función para crear un archivo de imagen privado en el directorio de imágenes específico de la aplicación
fun crearArchivoImagenPrivado(contexto: Context): File = File(
    contexto.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
    "${generarNombreSegunFechaHastasegundo()}.jpg"
)

// Función para capturar una foto usando el controlador de cámara del ciclo de vida proporcionado
fun capturarFoto(
    cameraController: LifecycleCameraController,
    archivo: File, // Archivo donde se guardará la imagen capturada
    contexto: Context,
    onImagenGuardada: (uri: Uri) -> Unit // Callback que se llama cuando la imagen se guarda exitosamente
) {
    val opciones = ImageCapture.OutputFileOptions.Builder(archivo).build()
    cameraController.takePicture(
        opciones,
        ContextCompat.getMainExecutor(contexto), // Ejecutor principal para manejar callbacks en el hilo principal
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                outputFileResults.savedUri?.let {
                    onImagenGuardada(it) // Llama al callback con la URI de la imagen guardada.
                }
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("Error::onImageCallback::onError", exception.message ?: "Error")
            }
        }
    )
}
