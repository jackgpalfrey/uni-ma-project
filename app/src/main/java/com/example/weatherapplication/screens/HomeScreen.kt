package com.example.weatherapplication.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherapplication.api.WeatherResponse
import com.example.weatherapplication.ui.theme.MyCustomTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(weatherData: WeatherResponse) {
    MyCustomTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top,
            ) {
                var text by rememberSaveable { mutableStateOf("") }
                var active by rememberSaveable { mutableStateOf(false) }

                SearchBar(
                    modifier = Modifier.fillMaxWidth(),
                    query = text,
                    placeholder = { Text(weatherData.name) },
                    onQueryChange = { text = it },
                    onSearch = { active = false },
                    active = active,
                    onActiveChange = {
                        active = it
                    }
                ) {
                    // Search result shown when active
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(4) {
                            // Search result
                        }
                    }
                }
            }
            Column(
                modifier = Modifier.fillMaxSize().padding(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    weatherData.name,
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.padding(vertical = 10.dp)
                )
                Text(
                    "Description: " + weatherData.weather[0].description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 15.dp)
                )
                Text(
                    "Temp: " + weatherData.main.temp + "°C feels like " + weatherData.main.feels_like + "°C",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 15.dp)
                )

                Text(
                    "" + weatherData.main.temp_min + "°C - " + weatherData.main.temp_max + "°C",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 15.dp)
                )
                Text(
                    "Humidity: " + weatherData.main.humidity + "%",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 15.dp)
                )
                Text(
                    "Pressure: " + weatherData.main.pressure + "mBar",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 15.dp)
                )
                Text(
                    "Wind: " + weatherData.wind.deg + " at " + weatherData.wind.speed + "kts, gusting " + weatherData.wind.gust + "kts",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 15.dp)
                )
                Text(
                    "Sunrise: " + weatherData.sys.sunrise + ", Sunset: " + weatherData.sys.sunset,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 15.dp)
                )
                Text(
                    "Data: " + weatherData,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 15.dp)
                )
                /*
                    Button(
                        onClick = { /* Do something! */ },
                        contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                    ) {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = "Localized description",
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text("Search")
                    }
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .height(200.dp)
                            .padding(horizontal = 0.dp, vertical = 0.dp)
                            .clip(MaterialTheme.shapes.large)
                    )*/
            }
        }
    }
}