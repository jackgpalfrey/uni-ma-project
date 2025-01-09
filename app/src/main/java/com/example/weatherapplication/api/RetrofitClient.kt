package com.example.weatherapplication.api

import com.example.weatherapplication.api.responses.AirQualityResponse
import com.example.weatherapplication.api.responses.WeatherForecastResponse
import com.example.weatherapplication.api.responses.WeatherResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object RetrofitClient {
    private const val API_URL = "https://api.openweathermap.org"

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
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): Call<WeatherResponse>

    @GET("data/2.5/air_pollution")
    fun getAirPollution(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): Call<AirQualityResponse>

    @GET("data/2.5/forecast")
    fun getFiveDayForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): Call<WeatherForecastResponse>

    @GET("data/2.5/forecast")
    fun getForecastByCity(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): Call<WeatherForecastResponse>

    @GET("data/2.5/weather")
    fun getWeatherByCity(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
    ): Call<WeatherResponse>
}