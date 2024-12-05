package com.example.weatherapplication.api.forecast

import com.example.weatherapplication.api.API_KEY
import com.example.weatherapplication.api.RetrofitClient
import com.example.weatherapplication.api.WeatherResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherHandlerForecast {
    // Fetches weather data for a given latitude and longitude
    fun fetchWeather(lat: Double, lon: Double, callback: WeatherCallback) {
        val call = RetrofitClient.weatherService.getCurrentWeather(lat, lon, API_KEY)

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    if (weatherResponse != null) {
                        callback.onSuccess(weatherResponse)
                    } else {
                        callback.onError("Empty response from server.")
                    }
                } else {
                    callback.onError("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                callback.onError("Failed to fetch weather: ${t.message}")
            }
        })
    }

    interface WeatherCallback {
        fun onSuccess(weatherResponse: WeatherResponse)
        fun onError(errorMessage: String)
    }
}