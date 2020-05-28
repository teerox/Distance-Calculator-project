package com.example.riby_distance_calculator.datasource

import androidx.lifecycle.LiveData
import com.example.riby_distance_calculator.database.DistanceDAO
import com.example.riby_distance_calculator.model.Distance
import javax.inject.Inject

class LocalDataSource @Inject constructor(private val distanceDAO: DistanceDAO){


    suspend fun save(item:Distance){
        distanceDAO.insertAll(item)
    }

    fun getAllFarmers(): LiveData<List<Distance>> {
        return distanceDAO.getAll()
    }
}