package com.example.riby_distance_calculator

import android.app.Application
import com.example.riby_distance_calculator.di.ApplicationComponents
import com.example.riby_distance_calculator.di.DaggerApplicationComponents

import com.example.riby_distance_calculator.di.module.DatabaseModule


class BaseApplication : Application() {

    lateinit var component: ApplicationComponents

    override fun onCreate() {
        super.onCreate()
        component = DaggerApplicationComponents.builder().databaseModule(DatabaseModule(this)).build()

    }

}