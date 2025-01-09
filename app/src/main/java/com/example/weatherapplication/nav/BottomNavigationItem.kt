package com.example.weatherapplication.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.weatherapplication.Views

data class BottomNavigationItem(
    val label : String = "",
    val icon : ImageVector = Icons.Filled.Home,
    val route : String = ""
) {
    fun bottomNavigationItems() : List<BottomNavigationItem> {
        return listOf(
            BottomNavigationItem(
                label = "Home",
                icon = Icons.Filled.Home,
                route = Views.Home.route
            ),
            BottomNavigationItem(
                label = "Map",
                icon = Icons.Filled.LocationOn,
                route = Views.Map.route
            ),
            BottomNavigationItem(
                label = "Forecast",
                icon = Icons.Filled.DateRange,
                route = Views.Forecast.route
            ),
            BottomNavigationItem(
                label = "Settings",
                icon = Icons.Filled.Settings,
                route = Views.Settings.route
            ),
        )
    }
}
