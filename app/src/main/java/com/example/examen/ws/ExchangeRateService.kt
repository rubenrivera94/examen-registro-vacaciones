package com.example.examen.ws

import com.example.examen.models.ExchangeRateResponse
import retrofit2.Response
import retrofit2.http.GET

/**
 * Interfaz que define los métodos para interactuar con el servicio de la API
 * relacionado con las tasas de cambio.
 */
interface ExchangeRateService {

    /**
     * Solicita la tasa de cambio del dólar desde la API.
     * La anotación @GET indica el endpoint al que se realiza la solicitud.
     * El método está marcado como 'suspend' para ser llamado desde corutinas,
     * permitiendo operaciones asincrónicas sin bloquear el hilo principal.
     *
     * @return Un objeto Response que contiene la respuesta de la API,
     *         con los datos de la tasa de cambio encapsulados en ExchangeRateResponse.
     */
    @GET("dolar")
    suspend fun getExchangeRate(): Response<ExchangeRateResponse>
}
