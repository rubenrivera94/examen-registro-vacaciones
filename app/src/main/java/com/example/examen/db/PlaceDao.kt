package com.example.examen.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.examen.models.PlaceEntity

@Dao
interface PlaceDao {
    @Query("SELECT * FROM places ORDER BY `order`")
    fun getAllPlaces(): List<PlaceEntity>

    @Insert
    suspend fun insertPlace(place: PlaceEntity)

    @Update
    suspend fun updatePlace(place: PlaceEntity)

    @Delete
    suspend fun deletePlace(place: PlaceEntity)
}