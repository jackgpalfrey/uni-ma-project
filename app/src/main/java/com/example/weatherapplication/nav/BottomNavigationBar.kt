package com.example.weatherapplication.nav

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.weatherapplication.Views
import com.example.weatherapplication.api.responses.AirQualityResponse
import com.example.weatherapplication.api.responses.WeatherForecastResponse
import com.example.weatherapplication.api.responses.WeatherResponse
import com.example.weatherapplication.screens.ForecastScreen2
import com.example.weatherapplication.screens.HomeScreen
import com.example.weatherapplication.screens.MapScreen
import com.example.weatherapplication.screens.SettingsScreen
import com.example.weatherapplication.ui.theme.ThemeViewModel


@Composable
fun BottomNavigationBar(weatherData: WeatherResponse, airData: AirQualityResponse, forecastData: WeatherForecastResponse, userLongitude: Double, userLatitude: Double, themeViewModel: ThemeViewModel, isDarkMode: Boolean) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                BottomNavigationItem().bottomNavigationItems().forEachIndexed { _, navigationItem ->
                    NavigationBarItem(
                        selected = navigationItem.route == currentDestination?.route,
                        label = {
                            Text(navigationItem.label)
                        },
                        icon = {
                            Icon(
                                navigationItem.icon,
                                contentDescription = navigationItem.label
                            )
                        },
                        onClick = {
                            navController.navigate(navigationItem.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Views.Home.route,
            modifier = Modifier.padding(paddingValues = paddingValues)
        ) {
            composable(Views.Home.route) {
                HomeScreen(weatherData, airData)
            }
            composable(Views.Map.route) {
                MapScreen(userLongitude, userLatitude, themeViewModel, isDarkMode)
            }
            composable(Views.Forecast.route) {
                ForecastScreen2(userLatitude, userLongitude)
            }
            composable(Views.Settings.route) {
                SettingsScreen(
                    weatherData,
                    airData,
                    userLongitude,
                    userLatitude,
                    themeViewModel
                )
            }
        }
    }
}
