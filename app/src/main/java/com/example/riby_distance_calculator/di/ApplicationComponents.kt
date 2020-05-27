package com.example.riby_distance_calculator.di


import com.example.riby_distance_calculator.BaseApplication
import com.example.riby_distance_calculator.di.module.DatabaseModule
import com.example.riby_distance_calculator.screen.MainActivity
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [DatabaseModule::class])
interface ApplicationComponents {

    fun inject(application: BaseApplication)
    fun inject(application: MainActivity)

}