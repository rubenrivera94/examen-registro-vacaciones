package com.example.examen.models

import com.squareup.moshi.Json

/**
 * Data class que representa un tipo de cambio individual.
 * Utiliza la librería Moshi para la deserialización de JSON.
 */
data class ExchangeRate(
    /**
     * El valor del tipo de cambio en formato numérico.
     * Se mapea desde el campo "valor" del JSON.
     */
    @Json(name = "valor") val value: Double,

    /**
     * La fecha del tipo de cambio en formato de cadena.
     * Se mapea desde el campo "fecha" del JSON.
     */
    @Json(name = "fecha") val date: String?
)
