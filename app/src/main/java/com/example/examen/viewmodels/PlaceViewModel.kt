package com.example.examen.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examen.db.PlaceDao
import com.example.examen.models.PlaceEntity
import com.example.examen.ws.Factory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel para gestionar los datos y operaciones relacionadas con los lugares.
 *
 * @property placeDao DAO para acceder a la base de datos de lugares.
 */
class PlaceViewModel(private val placeDao: PlaceDao) : ViewModel() {

    // MutableLiveData que almacena la lista de lugares.
    private val _places = MutableLiveData<List<PlaceEntity>>()

    // LiveData expuesto que permite observar la lista de lugares.
    val places: LiveData<List<PlaceEntity>> get() = _places

    // Inicializa el ViewModel cargando los lugares desde la base de datos.
    init {
        loadPlaces()
    }

    /**
     * Carga la lista de lugares desde la base de datos en un hilo de IO.
     */
    private fun loadPlaces() {
        viewModelScope.launch {
            // Ejecuta la operación de carga en un hilo de IO.
            val placesFromDb = withContext(Dispatchers.IO) {
                placeDao.getAllPlaces() ?: emptyList() // Obtiene todos los lugares o una lista vacía si no hay datos.
            }
            _places.postValue(placesFromDb) // Actualiza el LiveData con la lista de lugares.
        }
    }

    /**
     * Añade un nuevo lugar a la base de datos y recarga la lista de lugares.
     *
     * @param name Nombre del lugar.
     * @param imageUrl URL de la imagen del lugar.
     * @param latitude Latitud del lugar.
     * @param longitude Longitud del lugar.
     * @param order Orden del lugar.
     * @param accommodationCost Costo de alojamiento en CLP.
     * @param transportationCost Costo de transporte en CLP.
     * @param comments Comentarios sobre el lugar.
     */
    fun addPlace(name: String, imageUrl: String, latitude: Double, longitude: Double, order: Int, accommodationCost: Int, transportationCost: Int, comments: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                // Inserta el nuevo lugar en la base de datos.
                placeDao.insertPlace(PlaceEntity(0, name, imageUrl, latitude, longitude, order, accommodationCost, transportationCost, comments))
            }
            loadPlaces() // Recarga la lista de lugares para incluir el nuevo lugar.
        }
    }

    /**
     * Actualiza un lugar existente en la base de datos y recarga la lista de lugares.
     *
     * @param place El lugar con los datos actualizados.
     */
    fun updatePlace(place: PlaceEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                // Actualiza el lugar en la base de datos.
                placeDao.updatePlace(place)
            }
            loadPlaces() // Recarga la lista de lugares para reflejar la actualización.
        }
    }

    /**
     * Elimina un lugar de la base de datos y recarga la lista de lugares.
     *
     * @param place El lugar que se desea eliminar.
     */
    fun deletePlace(place: PlaceEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                // Elimina el lugar de la base de datos.
                placeDao.deletePlace(place)
            }
            loadPlaces() // Recarga la lista de lugares para reflejar la eliminación.
        }
    }

    /**
     * Obtiene un lugar por su ID.
     *
     * @param id El ID del lugar.
     * @return El lugar con el ID especificado o null si no se encuentra.
     */
    fun getPlaceById(id: Int?): PlaceEntity? {
        return _places.value?.find { it.id == id } // Busca el lugar en la lista de lugares.
    }

    /**
     * Convierte un monto en CLP a USD utilizando la tasa de cambio obtenida desde un servicio web.
     *
     * @param clp El monto en CLP.
     * @return El monto convertido a USD.
     */
    suspend fun convertToUSD(clp: Int): Double {
        return withContext(Dispatchers.IO) {
            val exchangeRateService = Factory.createExchangeRateService()
            val response = exchangeRateService.getExchangeRate()
            if (response.isSuccessful) {
                // Obtiene la tasa de cambio y realiza la conversión.
                val rate = response.body()?.series?.firstOrNull()?.value ?: 1.0
                clp / rate
            } else {
                0.0 // Retorna 0 si la solicitud falla.
            }
        }
    }

    /**
     * Lógica para manejar los permisos concedidos.
     * Aquí se puede agregar la lógica que se desea ejecutar cuando los permisos han sido concedidos.
     */
    fun onPermissionsGranted() {
        Log.d("PlaceViewModel", "Permisos concedidos") // Log de la concesión de permisos.
    }
}
