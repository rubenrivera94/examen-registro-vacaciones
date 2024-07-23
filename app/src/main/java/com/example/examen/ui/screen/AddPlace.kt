package com.example.examen.ui.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.examen.R
import com.example.examen.models.PlaceEntity
import com.example.examen.viewmodels.PlaceViewModel
import kotlinx.coroutines.launch
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

class AddPlaceActivity : ComponentActivity() {
    private val placeViewModel: PlaceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val placeId = intent?.getIntExtra("PLACE_ID", -1)
            val place = placeId?.let { placeViewModel.getPlaceById(it) }
            AgregarLugarUI(
                navController = navController,
                viewModel = placeViewModel,
                place = place,
                onSave = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarLugarUI(navController: NavController, viewModel: PlaceViewModel, place: PlaceEntity?, onSave: () -> Unit) {
    // Obtiene el contexto actual
    val contexto = LocalContext.current
    // Alcance de la corrutina para operaciones asincrónicas
    val alcanceCorrutina = rememberCoroutineScope()

    // Estado mutable para los campos del formulario
    val name = remember { mutableStateOf(place?.name ?: "") }
    val imageUrl = remember { mutableStateOf(place?.imageUrl ?: "") }
    val latLong = remember { mutableStateOf(place?.latitude?.toString()?.plus(", ") + place?.longitude?.toString() ?: "") }
    val order = remember { mutableStateOf(place?.order?.toString() ?: "") }
    val accommodationCost = remember { mutableStateOf(place?.accommodationCost?.toString() ?: "") }
    val transportationCost = remember { mutableStateOf(place?.transportationCost?.toString() ?: "") }
    val comments = remember { mutableStateOf(place?.comments ?: "") }

    // Estado mutable para mensajes de error
    var errorMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            // Barra superior con título y botón de navegación hacia atrás
            TopAppBar(
                title = { Text(stringResource(R.string.title_agregar_lugar)) },
                navigationIcon = {
                    IconButton(onClick = {
                        // Redirige a la pantalla anterior cuando se presiona el botón de retroceso
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.button_volver))
                    }
                }
            )
        },
        content = { paddingValues ->
            // Contenido principal de la pantalla
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(value = name.value, onValueChange = { name.value = it }, label = { Text("Lugar") })
                TextField(value = imageUrl.value, onValueChange = { imageUrl.value = it }, label = { Text("Imagen Ref.") })
                TextField(value = latLong.value, onValueChange = { latLong.value = it }, label = { Text("Latitud, Longitud") })
                TextField(value = order.value, onValueChange = { order.value = it }, label = { Text("Orden") })
                TextField(value = accommodationCost.value, onValueChange = { accommodationCost.value = it }, label = { Text("Costo Alojamiento") })
                TextField(value = transportationCost.value, onValueChange = { transportationCost.value = it }, label = { Text("Costo Traslados") })
                TextField(value = comments.value, onValueChange = { comments.value = it }, label = { Text("Comentarios") })

                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                }

                Button(onClick = {
                    // Validar y procesar la entrada
                    try {
                        val latLon = latLong.value.split(",")
                        if (latLon.size != 2) throw NumberFormatException()

                        val lat = latLon[0].trim().toDouble()
                        val lon = latLon[1].trim().toDouble()
                        val ord = order.value.toInt()
                        val accCost = accommodationCost.value.toInt()
                        val transCost = transportationCost.value.toInt()

                        if (place == null) {
                            viewModel.addPlace(
                                name.value,
                                imageUrl.value,
                                lat,
                                lon,
                                ord,
                                accCost,
                                transCost,
                                comments.value
                            )
                        } else {
                            viewModel.updatePlace(
                                place.copy(
                                    name = name.value,
                                    imageUrl = imageUrl.value,
                                    latitude = lat,
                                    longitude = lon,
                                    order = ord,
                                    accommodationCost = accCost,
                                    transportationCost = transCost,
                                    comments = comments.value
                                )
                            )
                        }
                        onSave()
                    } catch (e: NumberFormatException) {
                        // Manejar errores de conversión y mostrar un mensaje
                        errorMessage = "Por favor ingrese valores válidos en el campo Latitud, Longitud, Orden, Costo Alojamiento y Costo Traslados."
                    }
                }) {
                    Text("Guardar")
                }
            }
        }
    )
}
