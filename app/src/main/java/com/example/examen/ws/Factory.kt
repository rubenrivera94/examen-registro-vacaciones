package com.example.examen.ws

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Objeto singleton que proporciona una instancia de Retrofit configurada
 * para realizar solicitudes a una API que utiliza JSON para la comunicación.
 */
object Factory {

    // Configura la librería Moshi para el procesamiento de JSON.
    // KotlinJsonAdapterFactory permite la deserialización de datos en clases de Kotlin.
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())  // Añade el adaptador específico para Kotlin.
        .build()  // Construye la instancia de Moshi.

    // Configura Retrofit para la comunicación con la API.
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://mindicador.cl/api/")  // URL base de la API.
        .addConverterFactory(MoshiConverterFactory.create(moshi))  // Añade Moshi como convertidor de JSON.
        .build()  // Construye la instancia de Retrofit.

    /**
     * Crea una instancia de ExchangeRateService usando Retrofit.
     * @return Una implementación de la interfaz ExchangeRateService.
     */
    fun createExchangeRateService(): ExchangeRateService {
        // Utiliza Retrofit para crear una implementación de la interfaz ExchangeRateService.
        return retrofit.create(ExchangeRateService::class.java)
    }
}
