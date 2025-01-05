package com.example.weatherapplication.screens

import android.annotation.SuppressLint
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
import com.example.weatherapplication.api.responses.AirQualityResponse
import com.example.weatherapplication.api.responses.Forecast
import com.example.weatherapplication.api.responses.WeatherForecastResponse
import com.example.weatherapplication.api.responses.WeatherResponse
import java.text.SimpleDateFormat
import java.util.*
/*
@Composable
fun ForecastScreen(
    weatherData: WeatherResponse,
    airQualityData: AirQualityResponse,
    forecastData: WeatherForecastResponse,
) {
    // TODO:    Might have to add the reload

    var selectedTab by remember { mutableIntStateOf(0) }

    val groupedForecasts = forecastData.list.groupBy { it.dt_txt.split(" ")[0] }.toMutableMap()
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    // Ensure current day is included until midnight
    if (!groupedForecasts.containsKey(today)) {
        val todayForecasts = forecastData.list.filter {
            it.dt_txt.startsWith(today)
        }
        if (todayForecasts.isNotEmpty()) {
            groupedForecasts[today] = todayForecasts
        }
    }

    val days = groupedForecasts.keys.sorted() // Ensure proper ordering

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth()
        ) {
            days.forEachIndexed { index, day ->
                val formattedDay = if (day == today) {
                    "Now"
                } else {
                    formatDayWithDate(day)
                }
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            formattedDay.replace(" ", "\n"),
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 13.9.sp,
                            lineHeight = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                )
            }
        }

        if (days[selectedTab] == today) {
            val city = forecastData.city
            DaylightHoursBar(city.sunrise, city.sunset)
        }

        val forecastsForDay = groupedForecasts[days[selectedTab]] ?: emptyList()
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(forecastsForDay) { forecast ->
                IntervalCard(forecast)
            }
        }
    }
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
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
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
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${forecast.main.temp}°C",
                style = MaterialTheme.typography.bodyLarge
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

fun formatDay(dateString: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormat = SimpleDateFormat("EEE", Locale.getDefault())
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
}*/