package com.example.examen.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButton as M3IconButton
import androidx.compose.material3.Icon as M3Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.placeholder
import coil.compose.AsyncImage
import com.example.examen.R
import com.example.examen.models.PlaceEntity
import com.example.examen.viewmodels.PlaceViewModel
import kotlinx.coroutines.launch

@Composable
fun PlaceListScreen(navController: NavController, viewModel: PlaceViewModel) {
    val places by viewModel.places.observeAsState(initial = emptyList())
    Column {
        LazyColumn {
            items(places) { place ->
                PlaceItem(place, navController, viewModel)
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = { navController.navigate("add_place") }) {
                Text(text = "+ Agregar Lugar")
            }
        }
    }
}


@Composable
fun PlaceItem(place: PlaceEntity, navController: NavController, viewModel: PlaceViewModel) {
    val coroutineScope = rememberCoroutineScope()
    var accommodationCostUSD by remember { mutableStateOf<String?>(null) }
    var transportationCostUSD by remember { mutableStateOf<String?>(null) }

    // Ejecutar en corutina
    LaunchedEffect(place) {
        coroutineScope.launch {
            val accommodationCost = viewModel.convertToUSD(place.accommodationCost)
            val transportationCost = viewModel.convertToUSD(place.transportationCost)
            // Convertir Double a String
            accommodationCostUSD = "%.2f".format(accommodationCost)
            transportationCostUSD = "%.2f".format(transportationCost)
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
    ) {
        Spacer(modifier = Modifier.width(20.dp))
        Box(
            modifier = Modifier.widthIn(min = 100.dp, max = 100.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = place.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth(),
                placeholder = painterResource(id = R.drawable.placeholder), // Añade un drawable para el placeholder
                error = painterResource(id = R.drawable.error) // Añade un drawable para el error
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        Column() {
            Text(text = place.name, fontWeight = FontWeight.Bold)
            Text(text = "Costo x Noche: ${place.accommodationCost} CLP - ${accommodationCostUSD ?: "Calculando..."} USD")
            Text(text = "Traslado: ${place.transportationCost} CLP - ${transportationCostUSD ?: "Calculando..."} USD")
            Row {
                M3IconButton(onClick = { navController.navigate("place_detail/${place.id}") }) {
                    M3Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Detalles")
                }
                M3IconButton(onClick = { navController.navigate("edit_place/${place.id}") }) {
                    M3Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar")
                }
                M3IconButton(onClick = { viewModel.deletePlace(place) }) {
                    M3Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar")
                }
            }
        }
    }
}


