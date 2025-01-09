package com.example.weatherapplication.api

import com.example.weatherapplication.api.responses.AirQualityResponse
import com.example.weatherapplication.api.responses.Weather
import com.example.weatherapplication.api.responses.WeatherForecastResponse
import com.example.weatherapplication.api.responses.WeatherResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val API_KEY: String = "f129e0a6aafbd7ec0c71ed808a7b7b53"

class WeatherHandler {
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

    // Fetches 5 day forecast
    fun fetchFiveDayForecast(lat: Double, lon: Double, callback: ForecastCallback) {
        val call = RetrofitClient.weatherService.getFiveDayForecast(lat, lon, API_KEY)

        call.enqueue(object : Callback<WeatherForecastResponse> {
            override fun onResponse(
                call: Call<WeatherForecastResponse>,
                response: Response<WeatherForecastResponse>
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

            override fun onFailure(call: Call<WeatherForecastResponse>, t: Throwable) {
                callback.onError("Failed to fetch forecast: ${t.message}")
            }
        })
    }

    // Fetch Air quality
    fun fetchAirQuality(lat: Double, lon: Double, callback: AirQualityCallback) {
        val call = RetrofitClient.weatherService.getAirPollution(lat, lon, API_KEY)

        call.enqueue(object : Callback<AirQualityResponse> {
            override fun onResponse(
                call: Call<AirQualityResponse>,
                response: Response<AirQualityResponse>
            ) {
                if (response.isSuccessful) {
                    val airQualityResponse = response.body()
                    if (airQualityResponse != null) {
                        callback.onSuccess(airQualityResponse)
                    } else {
                        callback.onError("Empty response from server.")
                    }
                } else {
                    callback.onError("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<AirQualityResponse>, t: Throwable) {
                callback.onError("Failed to fetch air quality: ${t.message}")
            }
        })
    }

    // Fetch forecast by city
    fun fetchForecastByCity(
        cityName: String,
        callback: ForecastCallback
    ) {
        val call = RetrofitClient.weatherService.getForecastByCity(cityName, API_KEY)
        call.enqueue(object : Callback<WeatherForecastResponse> {
            override fun onResponse(
                call: Call<WeatherForecastResponse>,
                response: Response<WeatherForecastResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    callback.onSuccess(response.body()!!)
                } else {
                    callback.onError("Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<WeatherForecastResponse>, t: Throwable) {
                callback.onError("Failure: ${t.message}")
            }
        })
    }

    // Fetch forecast by city
    fun fetchWeatherByCity(
        cityName: String,
        callback: WeatherCallback
    ) {
        val call = RetrofitClient.weatherService.getWeatherByCity(cityName, API_KEY)
        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    callback.onSuccess(response.body()!!)
                } else {
                    callback.onError("Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                callback.onError("Failure: ${t.message}")
            }
        })
    }

    interface WeatherCallback {
        fun onSuccess(weatherResponse: WeatherResponse)
        fun onError(errorMessage: String)
    }

    interface AirQualityCallback {
        fun onSuccess(airQualityResponse: AirQualityResponse)
        fun onError(errorMessage: String)
    }

    interface ForecastCallback {
        fun onSuccess(forecastResponse: WeatherForecastResponse)
        fun onError(errorMessage: String)
    }
}