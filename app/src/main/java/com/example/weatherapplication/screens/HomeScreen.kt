package com.example.weatherapplication.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.weatherapplication.api.WeatherHandler
import com.example.weatherapplication.api.responses.AirQualityResponse
import com.example.weatherapplication.api.responses.Weather
import com.example.weatherapplication.api.responses.WeatherResponse
import com.example.weatherapplication.common.format24hTimeFromTimestamp
import com.example.weatherapplication.common.getIconID
import com.example.weatherapplication.common.mpsToMph
import java.util.Locale
import kotlin.math.roundToInt



private fun getTimeBasedGradient(weatherData: WeatherResponse): List<Color>{
    val currentTimestamp = System.currentTimeMillis()
    val sunsetTimestamp = weatherData.sys.sunset
    val sunriseTimestamp = weatherData.sys.sunrise

    println("" + sunriseTimestamp + " " + currentTimestamp + " " + sunsetTimestamp)

    if (currentTimestamp > sunsetTimestamp) {
    }
    
    val blankGradient = listOf(
        Color(0x00FFFFFF), // Start color
        Color(0x00FFFFFF)  // End color
    )

    val nightGradient = listOf(
        Color(0x502a5298), // Start color
        Color(0x501e3c72)  // End color
    )


    val dayGradient = listOf(
        Color(0x5064B5F6), // Start color
        Color(0x500D47A1)  // End color
    )

    val sunriseGradient = listOf(
        Color(0x50fffc99), // Start color
        Color(0x50ff8f8f)  // End color
    )

    return blankGradient
}



@Composable
fun HomeScreen(
    geoWeatherData: WeatherResponse,
    geoAirQualityData: AirQualityResponse
) {
    var weatherData by remember { mutableStateOf(geoWeatherData) }
    var airQualityData by remember { mutableStateOf(geoAirQualityData) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }




    Surface(
        color = MaterialTheme.colorScheme.background,
    ) {
        when {
            isLoading -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator()
                }
            }
            errorMessage != null -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("$errorMessage", style = MaterialTheme.typography.bodyMedium)
                }
            }

            else -> {
        Column(

            modifier = Modifier.fillMaxSize().background(
                brush = Brush.verticalGradient(
                    colors = getTimeBasedGradient(weatherData)
                )
            )
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(15.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Search for places...") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        if (searchQuery.isNotBlank()) {
                            isLoading = true
                            isSearching = true

                            fetchWeatherByCity(
                                cityName = searchQuery,
                                onSuccess = {
                                    weatherData = it


                                    // After weather success, fetch air quality
                                    fetchAirQuality(
                                        weatherData = weatherData,
                                        onSuccess = {
                                            airQualityData = it
                                            isLoading = false
                                            searchQuery = ""
                                        },
                                        onError = {
                                            errorMessage = it
                                            isLoading = false
                                        })
                                },
                                onError = {
                                    errorMessage = it
                                    isLoading = false
                                }
                            )


                        } else {
                            weatherData = geoWeatherData
                            airQualityData = geoAirQualityData
                        }
                    }) {
                        if (searchQuery.isNotBlank()) {
                            Text("Search")
                        } else {
                            Text("Current")
                        }
                    }
                }
                Text(
                    weatherData.name,
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.padding(vertical = 10.dp)
                )
                Text(
                    weatherData.weather[0].description.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.getDefault()
                        ) else it.toString()
                    },
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 15.dp)
                )
                Row {
                    Text(
                        weatherData.main.temp.roundToInt().toString(),
                        style = MaterialTheme.typography.displayLarge,
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 0.dp)
                    )
                    Image(
                        painter = painterResource(
                            id = getIconID(
                                LocalContext.current,
                                weatherData.weather[0].icon
                            )
                        ),
                        contentDescription = "",

                        )

                }
                Text(
                    "Feels like " + weatherData.main.feels_like.roundToInt().toString() + "°",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 15.dp)
                )
                Text(
                    "High " + weatherData.main.temp_max.roundToInt()
                        .toString() + "°" + " - Low " + weatherData.main.temp_min.roundToInt()
                        .toString() + "°",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 0.dp, horizontal = 15.dp)
                        .offset(y = (-8).dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,


                    ) {
                    DetailsCard("Precipitation", (weatherData.rain?.toString() ?: "0") + "mm")
                    DetailsCard("Cloud coverage", (weatherData.clouds.all.toString() ?: "0") + "%")
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,


                    ) {
                    DetailsCard("Humidity", (weatherData.main.humidity.toString() ?: "0") + "%")
                    DetailsCard("Pressure", (weatherData.main.pressure.toString() ?: "0"))

                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,


                    ) {
                    DetailsCard(
                        "Wind",
                        (mpsToMph(weatherData.wind.speed).roundToInt().toString()) + "mph,",
                        "gusting " + mpsToMph(weatherData.wind.gust).roundToInt()
                            .toString() + "mph,",
                        "from " + (weatherData.wind.deg.toString()) + "°",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    DetailsCard(
                        "Sun",
                        "Sunrise: " + format24hTimeFromTimestamp(weatherData.sys.sunrise),
                        "Sunset: " + format24hTimeFromTimestamp(weatherData.sys.sunset),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }



                Card(
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 15.dp).fillMaxWidth()

                ) {
                    // Display Air Quality Data
                    airQualityData.let { aqData ->
                        if (aqData.list.isNotEmpty()) {
                            val currentAQ = aqData.list[0]
                            Text(
                                "Air Quality Index: ${currentAQ.main.aqi}",
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.padding(vertical = 10.dp)
                                    .align(Alignment.CenterHorizontally)
                            )

                            Text(
                                "Pollutants:",
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(top = 10.dp)
                                    .align(Alignment.CenterHorizontally)
                            )

                            with(currentAQ.components) {
                                Text(
                                    "CO: $co μg/m³",
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                                Text(
                                    "NO2: $no2 μg/m³",
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                                Text(
                                    "O3: $o3 μg/m³",
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                                Text(
                                    "PM2.5: $pm2_5 μg/m³",
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                                Text(
                                    "PM10: $pm10 μg/m³",
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                            }
                        }
                    }

                }
            }
        }
            }
        }
    }
    }
@Composable
fun DetailsCard(title: String, vararg values: String, style: TextStyle = MaterialTheme.typography.displaySmall) {
    Card(
        modifier = Modifier.padding(vertical = 0.dp, horizontal = 15.dp).width(150.dp)
            .aspectRatio(1f)

    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 10.dp)
        )
        Column(
            modifier = Modifier.fillMaxWidth().fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            for (value in values) {
                Text(
                    value,
                    style = style,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }

    }
}



private fun fetchWeatherByCity(
    cityName: String,
    onSuccess: (WeatherResponse) -> Unit,
    onError: (String) -> Unit
) {
    val weatherHandler = WeatherHandler()

    weatherHandler.fetchWeatherByCity(cityName, object : WeatherHandler.WeatherCallback {
        override fun onSuccess(weatherResponse: WeatherResponse) {
            onSuccess(weatherResponse)
        }

        override fun onError(errorMessage: String) {
            onError(errorMessage)
        }
    })

}

private fun fetchAirQuality(
    weatherData: WeatherResponse,
    onSuccess: (AirQualityResponse) -> Unit,
    onError: (String) -> Unit
) {
    val weatherHandler = WeatherHandler()

    weatherHandler.fetchAirQuality(weatherData.coord.lat, weatherData.coord.lon, object : WeatherHandler.AirQualityCallback {
        override fun onSuccess(airQualityResponse: AirQualityResponse) {
            onSuccess(airQualityResponse)
        }

        override fun onError(errorMessage: String) {
            onError(errorMessage)
        }
    })

}


