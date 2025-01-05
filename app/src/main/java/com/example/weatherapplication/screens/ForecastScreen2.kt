package com.example.weatherapplication.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapplication.api.WeatherHandler
import com.example.weatherapplication.api.responses.Forecast
import com.example.weatherapplication.api.responses.WeatherForecastResponse
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ForecastScreen2(userLatitude: Double, userLongitude: Double) {
    val coroutineScope = rememberCoroutineScope()
    var forecastData by remember { mutableStateOf<WeatherForecastResponse?>(null) }

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
                style = MaterialTheme.typography.titleSmall
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
                    Text("Error: $errorMessage", style = MaterialTheme.typography.bodyMedium)
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

                val days = groupedForecasts.keys.sorted()
                val pagerState = rememberPagerState(
                    initialPage = 0
                )

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
                                        style = MaterialTheme.typography.bodySmall,
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
    onSuccess: (WeatherForecastResponse) -> Unit,
    onError: (String) -> Unit
) {
    val weatherHandler = WeatherHandler()

    weatherHandler.fetchFiveDayForecast(lat, lon, object : WeatherHandler.ForecastCallback {
        override fun onSuccess(forecastResponse: WeatherForecastResponse) {
            onSuccess(forecastResponse)
        }

        override fun onError(errorMessage: String) {
            onError(errorMessage)
        }
    })
}

fun fetchWeatherByCity(
    cityName: String,
    onSuccess: (WeatherForecastResponse) -> Unit,
    onError: (String) -> Unit
) {
    val weatherHandler = WeatherHandler()
    weatherHandler.fetchWeatherByCity(cityName, object : WeatherHandler.ForecastCallback {
        override fun onSuccess(forecastResponse: WeatherForecastResponse) {
            onSuccess(forecastResponse)
        }

        override fun onError(errorMessage: String) {
            onError(errorMessage)
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
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Daylight Hours:",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Sunrise: $sunriseTime",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Sunset: $sunsetTime",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun IntervalCard(forecast: Forecast) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = format24HourTime(forecast.dt_txt),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${forecast.main.temp}°C",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = forecast.weather[0].main,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Precipitation: ${String.format("%.0f", forecast.pop * 100)}%",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Wind: ${forecast.wind.speed} m/s",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Humidity: ${forecast.main.humidity}%",
                style = MaterialTheme.typography.bodyMedium
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