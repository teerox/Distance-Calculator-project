package com.example.agromallapplication.utils

interface GetResult {
    fun onSuccess(result: String)
    fun onFailure(failed:String)
}