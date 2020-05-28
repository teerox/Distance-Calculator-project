package com.example.riby_distance_calculator.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.riby_distance_calculator.di.ViewModelKey
import com.example.riby_distance_calculator.screen.DistanceViewModel
import com.example.riby_distance_calculator.screen.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(DistanceViewModel::class)
    abstract fun bindViewModule(distance: DistanceViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory

}