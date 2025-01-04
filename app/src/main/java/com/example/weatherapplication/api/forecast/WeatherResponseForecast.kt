package com.example.weatherapplication.api.forecast

data class WeatherResponseForecast(
    val list: List<Forecast>,
    val city: City
)

data class Forecast(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    val pop: Double,
    val rain: Rain,
    val sys: Sys,
    val dt_txt: String // Timestamp as a string
)

data class Main(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val sea_level: Int?,
    val grnd_level: Int?,
    val humidity: Int,
    val temp_kf: Double? // Optional field for temperature correction
)

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class Clouds(
    val all: Int // Cloudiness percentage
)

data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double? // Optional gust speed
)

data class Rain(
    val `3h`: Double? // Rain volume for the last 3 hours
)

data class Sys(
    val pod: String // Part of the day (e.g., "n" for night, "d" for day)
)

data class City(
    val id: Int,
    val name: String,
    val coord: Coord,
    val country: String,
    val population: Int,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long
)

data class Coord(
    val lat: Double,
    val lon: Double
)
