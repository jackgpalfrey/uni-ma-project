package com.example.weatherapplication.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.weatherapplication.api.WeatherResponse
import com.example.weatherapplication.ui.theme.MyCustomTheme

@Composable
fun HomeScreen(weatherData: WeatherResponse) {
    MyCustomTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
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
                    "Data: " + weatherData.weather[0].description,
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