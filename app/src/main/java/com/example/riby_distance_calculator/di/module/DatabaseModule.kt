package com.example.riby_distance_calculator.di.module

import android.content.Context
import androidx.room.Room
import com.example.riby_distance_calculator.database.DistanceDAO
import com.example.riby_distance_calculator.database.DistanceDatabase
import com.example.riby_distance_calculator.BaseApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class DatabaseModule(private var application: BaseApplication){

    @Singleton
    @Provides
    fun context(): Context {
        return application
    }

    @Singleton
    @Provides
    internal fun provideRoomDatabase(context: Context): DistanceDatabase {
        return Room.databaseBuilder(context, DistanceDatabase::class.java, "distance-db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideDao(database: DistanceDatabase): DistanceDAO {
        return database.distanceDao()
    }

}