package com.example.riby_distance_calculator.repository

import androidx.lifecycle.LiveData
import com.example.riby_distance_calculator.datasource.LocalDataSource
import com.example.riby_distance_calculator.model.Distance
import javax.inject.Inject

class DistanceRepository @Inject constructor(private val dataSource: LocalDataSource){

    suspend fun save(item: Distance){
        dataSource.save(item)
    }

    fun getAllData(): LiveData<List<Distance>> {
        return dataSource.getAllFarmers()
    }
}