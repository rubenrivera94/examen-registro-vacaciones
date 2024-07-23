package com.example.examen.ui.screen

import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.examen.viewmodels.PlaceViewModel

/**
 * Configura la navegación de la aplicación utilizando Jetpack Compose.
 * Define las diferentes pantallas a las que se puede navegar y cómo se pasan los datos entre ellas.
 *
 * @param navController Controlador de navegación utilizado para manejar la navegación entre pantallas.
 * @param viewModel ViewModel que contiene la lógica de negocio y datos relacionados con los lugares.
 * @param lanzadorPermisos Lanzador de permisos utilizado para solicitar permisos en tiempo de ejecución.
 */
@Composable
fun MainNavGraph(
    navController: NavHostController,
    viewModel: PlaceViewModel,
    lanzadorPermisos: ActivityResultLauncher<Array<String>>
) {
    // Define el host de navegación que administra la navegación entre las diferentes pantallas
    NavHost(navController, startDestination = "place_list") {
        // Pantalla principal que muestra la lista de lugares
        composable("place_list") {
            PlaceListScreen(navController, viewModel)
        }

        // Pantalla para agregar un nuevo lugar
        composable("add_place") {
            AgregarLugarUI(
                navController = navController,
                viewModel = viewModel,
                place = null,  // No se pasa ningún lugar ya que estamos agregando uno nuevo
                onSave = {
                    // Acción a realizar cuando se guarda el nuevo lugar, regresa a la pantalla anterior
                    navController.popBackStack()
                }
            )
        }

        // Pantalla para editar un lugar existente, identificada por el ID del lugar
        composable("edit_place/{placeId}") { backStackEntry ->
            val placeId = backStackEntry.arguments?.getString("placeId")?.toInt()
            val place = viewModel.getPlaceById(placeId)
            if (place != null) {
                EditPlaceScreen(viewModel, place, navController)
            }
        }

        // Pantalla que muestra los detalles de un lugar, identificada por el ID del lugar
        composable("place_detail/{placeId}") { backStackEntry ->
            val placeId = backStackEntry.arguments?.getString("placeId")?.toInt()
            val place = viewModel.getPlaceById(placeId)
            if (place != null) {
                PlaceDetailScreen(
                    place = place,
                    viewModel = viewModel,
                    navController = navController,
                    lanzadorPermisos = lanzadorPermisos
                )
            }
        }

        // Pantalla para capturar una imagen usando la cámara
        composable("camera_screen") {
            CameraScreen(
                navController = navController,
                lanzadorPermisos = lanzadorPermisos,
                onImageCaptured = { uri ->
                    // Manejo de la URI de la imagen capturada (se puede realizar alguna acción adicional aquí)
                }
            )
        }
    }
}
