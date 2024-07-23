package com.example.examen

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.examen.db.AppDatabase
import com.example.examen.ui.screen.MainNavGraph
import com.example.examen.viewmodels.PlaceViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Configura el agente de usuario para osmdroid
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    }
}

class MainActivity : ComponentActivity() {
    private lateinit var placeViewModel: PlaceViewModel

    private val lanzadorPermisos = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.CAMERA] == true && permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            placeViewModel.onPermissionsGranted() // Llama sin argumentos
        } else {
            Toast.makeText(this, "Se requieren permisos para la cámara y la ubicación", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar la base de datos y el DAO antes de verificar los permisos
        val appDatabase = AppDatabase.getInstance(this)
        val placeDao = appDatabase.placeDao()

        // Crear el ViewModel
        placeViewModel = PlaceViewModel(placeDao)

        // Verificar permisos
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            lanzadorPermisos.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION))
        } else {
            placeViewModel.onPermissionsGranted() // Llama sin argumentos
        }

        setContent {
            val navController = rememberNavController()
            val viewModel = remember { placeViewModel }
            MainNavGraph(navController, viewModel, lanzadorPermisos)
        }
    }
}
