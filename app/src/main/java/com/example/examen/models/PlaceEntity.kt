package com.example.examen.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "places")
data class PlaceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val imageUrl: String,
    val latitude: Double,
    val longitude: Double,
    val order: Int,
    val accommodationCost: Int,
    val transportationCost: Int,
    val comments: String
)
