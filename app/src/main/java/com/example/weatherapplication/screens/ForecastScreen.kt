package com.example.weatherapplication.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.weatherapplication.api.forecast.Forecast
import com.example.weatherapplication.api.forecast.WeatherHandlerForecast
import com.example.weatherapplication.api.forecast.WeatherResponseForecast
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ForecastScreen(userLatitude: Double, userLongitude: Double) {
    val coroutineScope = rememberCoroutineScope()
    var forecastData by remember { mutableStateOf<WeatherResponseForecast?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var currentCity by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        fetchWeatherForLocation(userLatitude, userLongitude, onSuccess = {
            forecastData = it
            currentCity = it.city.name
            isLoading = false
        }, onError = {
            errorMessage = it
            isLoading = false
        })
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search Bar
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
                placeholder = { Text("Search for a city") },
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
                            forecastData = it
                            currentCity = it.city.name
                            isLoading = false
                        },
                        onError = {
                            errorMessage = it
                            isLoading = false
                        }
                    )
                }
            }) {
                Text("Search")
            }
        }

        // Display current city
        if (currentCity.isNotBlank()) {
            Text(
                text = "Weather for: $currentCity",
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h6
            )
        }

        when {
            isLoading -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator()
                }
            }
            errorMessage != null -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("Error: $errorMessage", style = MaterialTheme.typography.body1)
                }
            }
            forecastData != null -> {
                val groupedForecasts = forecastData!!.list.groupBy { it.dt_txt.split(" ")[0] }.toMutableMap()
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                // Ensure current day is included until midnight
                if (!groupedForecasts.containsKey(today)) {
                    val todayForecasts = forecastData!!.list.filter {
                        it.dt_txt.startsWith(today)
                    }
                    if (todayForecasts.isNotEmpty()) {
                        groupedForecasts[today] = todayForecasts
                    }
                }

                val days = groupedForecasts.keys.sorted() // Ensure proper ordering
                val pagerState = rememberPagerState(initialPage = 0)

                Column(modifier = Modifier.fillMaxSize()) {
                    // Top Tab Bar
                    TabRow(
                        selectedTabIndex = pagerState.currentPage,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        days.forEachIndexed { index, day ->
                            val formattedDay = if (day == today) {
                                "Today"
                            } else {
                                formatDayWithDate(day)
                            }
                            Tab(
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                },
                                text = {
                                    Text(
                                        formattedDay.replace(" ", "\n"),
                                        style = MaterialTheme.typography.body1,
                                        fontSize = 13.sp,
                                        lineHeight = 16.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            )
                        }
                    }

                    // Pager for swiping between days
                    HorizontalPager(
                        count = days.size,
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        val forecastsForDay = groupedForecasts[days[page]] ?: emptyList()

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (days[page] == today) {
                                val city = forecastData!!.city
                                item {
                                    DaylightHoursBar(city.sunrise, city.sunset)
                                }
                            }
                            items(forecastsForDay) { forecast ->
                                IntervalCard(forecast)
                            }
                        }
                    }
                }
            }
        }
    }
}

fun fetchWeatherForLocation(
    lat: Double,
    lon: Double,
    onSuccess: (WeatherResponseForecast) -> Unit,
    onError: (String) -> Unit
) {
    val weatherHandler = WeatherHandlerForecast()
    weatherHandler.fetchFiveDayForecast(lat, lon, object : WeatherHandlerForecast.WeatherCallback {
        override fun onSuccess(forecastResponse: WeatherResponseForecast) {
            onSuccess(forecastResponse)
        }

        override fun onError(message: String) {
            onError(message)
        }
    })
}

fun fetchWeatherByCity(
    cityName: String,
    onSuccess: (WeatherResponseForecast) -> Unit,
    onError: (String) -> Unit
) {
    val weatherHandler = WeatherHandlerForecast()
    weatherHandler.fetchWeatherByCity(cityName, object : WeatherHandlerForecast.WeatherCallback {
        override fun onSuccess(forecastResponse: WeatherResponseForecast) {
            onSuccess(forecastResponse)
        }

        override fun onError(message: String) {
            onError(message)
        }
    })
}

@Composable
fun DaylightHoursBar(sunrise: Long, sunset: Long) {
    val sunriseTime = formatTimeWithoutAmPm(sunrise)
    val sunsetTime = formatTimeWithoutAmPm(sunset)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Daylight Hours:",
                style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Sunrise: $sunriseTime",
                style = MaterialTheme.typography.body2
            )
            Text(
                text = "Sunset: $sunsetTime",
                style = MaterialTheme.typography.body2
            )
        }
    }
}

@Composable
fun IntervalCard(forecast: Forecast) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = format24HourTime(forecast.dt_txt),
                style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${forecast.main.temp}°C",
                style = MaterialTheme.typography.body1
            )
            Text(
                text = forecast.weather[0].main,
                style = MaterialTheme.typography.body2
            )
            Text(
                text = "Precipitation: ${String.format("%.0f", forecast.pop * 100)}%",
                style = MaterialTheme.typography.body2
            )
            Text(
                text = "Wind: ${forecast.wind.speed} m/s",
                style = MaterialTheme.typography.body2
            )
            Text(
                text = "Humidity: ${forecast.main.humidity}%",
                style = MaterialTheme.typography.body2
            )
        }
    }
}

fun formatDayWithDate(dateString: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormat = SimpleDateFormat("EEE d/M", Locale.getDefault())
    return try {
        val date = inputFormat.parse(dateString)
        if (date != null) {
            outputFormat.format(date)
        } else {
            "Unknown"
        }
    } catch (e: Exception) {
        "Invalid"
    }
}

fun format24HourTime(dateString: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return try {
        val date = inputFormat.parse(dateString)
        if (date != null) {
            outputFormat.format(date)
        } else {
            "Unknown"
        }
    } catch (e: Exception) {
        "Invalid"
    }
}

fun formatTimeWithoutAmPm(unixTime: Long): String {
    val date = Date(unixTime * 1000) // Convert seconds to milliseconds
    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
    return format.format(date)
}
