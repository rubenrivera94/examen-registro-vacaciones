package com.example.examen.models

import com.squareup.moshi.Json

/**
 * Data class que representa la respuesta de la API para los tipos de cambio.
 * Utiliza la librería Moshi para la deserialización de JSON.
 */
data class ExchangeRateResponse(
    /**
     * Lista de tipos de cambio obtenidos en la respuesta de la API.
     * Se mapea desde el campo "serie" del JSON.
     */
    @Json(name = "serie") val series: List<ExchangeRate>
)
