package com.example.weatherapplication.api

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Encapsulation: The WeatherHandler class abstracts away the details of the Retrofit API calls.
 * Reusability: The WeatherHandler class can be used across multiple Activities, Fragments, or ViewModels.
 * Separation of Concerns: The WeatherHandler focuses solely on fetching weather data,
 *  while the caller handles UI or further processing.
 *
 */
class WeatherHandler(private val apiKey: String) {

    // Fetches weather data for a given latitude and longitude
    fun fetchWeather(lat: Double, lon: Double, callback: WeatherCallback) {
        val call = RetrofitClient.weatherService.getCurrentWeather(lat, lon, apiKey)

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
