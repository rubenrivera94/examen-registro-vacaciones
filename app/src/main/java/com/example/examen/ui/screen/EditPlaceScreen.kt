package com.example.examen.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.examen.R
import com.example.examen.models.PlaceEntity
import com.example.examen.viewmodels.PlaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPlaceScreen(viewModel: PlaceViewModel, place: PlaceEntity?, navController: NavController) {
    val name = remember { mutableStateOf(place?.name ?: "") }
    val imageUrl = remember { mutableStateOf(place?.imageUrl ?: "") }
    val latLong = remember { mutableStateOf(place?.let { "${it.latitude},${it.longitude}" } ?: "") }
    val order = remember { mutableStateOf(place?.order?.toString() ?: "") }
    val accommodationCost = remember { mutableStateOf(place?.accommodationCost?.toString() ?: "") }
    val transportationCost = remember { mutableStateOf(place?.transportationCost?.toString() ?: "") }
    val comments = remember { mutableStateOf(place?.comments ?: "") }
    var errorMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            // Barra superior con título y botón de navegación hacia atrás
            TopAppBar(
                title = { Text(stringResource(R.string.title_editar_lugar)) },
                navigationIcon = {
                    IconButton(onClick = {
                        // Navega hacia atrás en la pila de navegación
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
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = name.value,
                    onValueChange = { name.value = it },
                    label = { Text("Lugar") }
                )
                TextField(
                    value = imageUrl.value,
                    onValueChange = { imageUrl.value = it },
                    label = { Text("Imagen Ref.") }
                )
                TextField(
                    value = latLong.value,
                    onValueChange = { latLong.value = it },
                    label = { Text("Latitud, Longitud") }
                )
                TextField(
                    value = order.value,
                    onValueChange = { order.value = it },
                    label = { Text("Orden") }
                )
                TextField(
                    value = accommodationCost.value,
                    onValueChange = { accommodationCost.value = it },
                    label = { Text("Costo Alojamiento") }
                )
                TextField(
                    value = transportationCost.value,
                    onValueChange = { transportationCost.value = it },
                    label = { Text("Costo Traslados") }
                )
                TextField(
                    value = comments.value,
                    onValueChange = { comments.value = it },
                    label = { Text("Comentarios") }
                )

                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                }

                Button(onClick = {
                    // Validar y procesar la entrada
                    try {
                        val latLon = latLong.value.split(",").map { it.trim() }
                        if (latLon.size != 2) throw NumberFormatException()

                        val lat = latLon[0].toDouble()
                        val lon = latLon[1].toDouble()
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
                        navController.popBackStack()
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
