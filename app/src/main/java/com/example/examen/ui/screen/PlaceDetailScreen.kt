package com.example.examen.ui.screen

import android.Manifest
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.examen.R
import com.example.examen.models.PlaceEntity
import com.example.examen.viewmodels.PlaceViewModel
import kotlinx.coroutines.launch
import org.osmdroid.views.MapView
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceDetailScreen(
    place: PlaceEntity,
    viewModel: PlaceViewModel,
    navController: NavController,
    lanzadorPermisos: ActivityResultLauncher<Array<String>>
) {
    var accommodationCostUSD by remember { mutableStateOf<String?>(null) }
    var transportationCostUSD by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(place) {
        coroutineScope.launch {
            try {
                accommodationCostUSD = "%.2f".format(viewModel.convertToUSD(place.accommodationCost))
                transportationCostUSD = "%.2f".format(viewModel.convertToUSD(place.transportationCost))
            } catch (e: Exception) {
                accommodationCostUSD = "Error"
                transportationCostUSD = "Error"
            }
        }
        lanzadorPermisos.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_detalle_lugar)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.button_volver))
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = place.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(20.dp))
                AsyncImage(
                    model = place.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .height(180.dp)
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(text = "Costo x Noche", fontWeight = FontWeight.Bold)
                        Text(text = "${place.accommodationCost} CLP - ${accommodationCostUSD ?: "Calculando..."} USD")
                    }
                    Column {
                        Text(text = "Traslados", fontWeight = FontWeight.Bold)
                        Text(text = "${place.transportationCost} CLP - ${transportationCostUSD ?: "Calculando..."} USD")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Comentarios:", fontWeight = FontWeight.Bold)
                Text(text = place.comments)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = {
                        navController.navigate("camera_screen") {
                            // Asegúrate de que la pantalla de cámara no esté en la pila de navegación
                            popUpTo("place_detail_screen") { inclusive = false }
                        }
                    }) {
                        Icon(imageVector = Icons.Filled.CameraAlt, contentDescription = "Agregar Foto") // Usando el ícono de cámara
                    }
                    IconButton(onClick = { /* Navegar a la pantalla de edición */ }) {
                        Icon(imageVector = Icons.Filled.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = { viewModel.deletePlace(place) }) {
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = "Eliminar")
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))

                Box(
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth()
                ) {
                    AndroidView(
                        factory = { context ->
                            MapView(context).apply {
                                Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
                                setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
                                isTilesScaledToDpi = true
                                controller.setZoom(10.0)
                                controller.setCenter(GeoPoint(place.latitude, place.longitude))

                                val marker = Marker(this)
                                marker.position = GeoPoint(place.latitude, place.longitude)
                                marker.title = place.name
                                overlays.add(marker)

                                invalidate()
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    )
}
