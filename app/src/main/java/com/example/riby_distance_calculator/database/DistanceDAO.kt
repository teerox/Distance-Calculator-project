package com.example.riby_distance_calculator.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.riby_distance_calculator.model.Distance

@Dao
interface DistanceDAO {
    @Query("SELECT * FROM distance ORDER BY uid DESC")
    fun getAll(): LiveData<List<Distance>>

    @Insert
    fun insertAll(vararg farmer: Distance)



}