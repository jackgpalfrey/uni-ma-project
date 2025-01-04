package com.example.weatherapplication.api.forecast

import com.example.weatherapplication.api.API_KEY
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherHandlerForecast {
    fun fetchFiveDayForecast(lat: Double, lon: Double, callback: WeatherCallback) {
        val call = RetrofitClient.weatherService.getFiveDayForecast(lat, lon, API_KEY)

        call.enqueue(object : Callback<WeatherResponseForecast> {
            override fun onResponse(
                call: Call<WeatherResponseForecast>,
                response: Response<WeatherResponseForecast>
            ) {
                if (response.isSuccessful) {
                    val forecastResponse = response.body()
                    if (forecastResponse != null) {
                        callback.onSuccess(forecastResponse)
                    } else {
                        callback.onError("Empty response from server.")
                    }
                } else {
                    callback.onError("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<WeatherResponseForecast>, t: Throwable) {
                callback.onError("Failed to fetch forecast: ${t.message}")
            }
        })
    }

    interface WeatherCallback {
        fun onSuccess(forecastResponse: WeatherResponseForecast)
        fun onError(errorMessage: String)
    }
}


