package com.example.weatherapplication.nav

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.weatherapplication.Views
import com.example.weatherapplication.api.WeatherResponse
import com.example.weatherapplication.screens.ForecastScreen
import com.example.weatherapplication.screens.HomeScreen
import com.example.weatherapplication.screens.MapScreen

@Composable
fun BottomNavigationBar(weatherData: WeatherResponse, userLongitude: Double, userLatitude: Double) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                BottomNavigationItem().bottomNavigationItems().forEach { navigationItem ->
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
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Views.Home.route) {
                HomeScreen(weatherData)
            }
            composable(Views.Map.route) {
                MapScreen(userLongitude, userLatitude)
            }
            composable(Views.Forecast.route) {
                ForecastScreen(userLongitude, userLatitude)
            }
        }
    }
}
