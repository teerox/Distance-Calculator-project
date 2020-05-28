package com.example.riby_distance_calculator.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.riby_distance_calculator.database.DistanceDAO
import com.example.riby_distance_calculator.model.Distance


@Database(entities = [Distance::class],version = 1,exportSchema = false)
abstract class DistanceDatabase:RoomDatabase(){
    abstract fun distanceDao(): DistanceDAO
}