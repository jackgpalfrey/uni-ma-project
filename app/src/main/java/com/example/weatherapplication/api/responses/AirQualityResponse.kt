package com.example.weatherapplication.api.responses

data class AirQualityResponse(
    val coord: Coord,
    val list: List<AirQualityItem>
)

data class AirQualityItem(
    val dt: Long,
    val main: AirQualityMain,
    val components: Components
)

data class AirQualityMain(
    val aqi: Int
)

data class Components(
    val co: Double,
    val no: Double,
    val no2: Double,
    val o3: Double,
    val so2: Double,
    val pm2_5: Double,
    val pm10: Double,
    val nh3: Double
)