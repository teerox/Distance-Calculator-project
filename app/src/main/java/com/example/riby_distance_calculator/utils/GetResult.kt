package com.example.riby_distance_calculator.utils

interface GetResult {
    fun onSuccess(result: String)
    fun onFailure(failed:String)
}