package com.example.weatherapplication.api

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object RetrofitClient {
    private const val API_URL = "https://api.openweathermap.org/"

    val weatherService: WeatherService by lazy {
        Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherService::class.java)
    }
}

// OpenWeather password; uweWeatherApp_123!
interface WeatherService {
    @GET("data/2.5/weather")
    fun getCurrentWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String
    ): Call<WeatherResponse>
}