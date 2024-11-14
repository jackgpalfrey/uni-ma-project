package com.example.weatherapplication

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

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
                icon = Icons.Filled.Search,
                route = Views.Map.route
            ),
            BottomNavigationItem(
                label = "Places",
                icon = Icons.Filled.AccountCircle,
                route = Views.Places.route
            ),
        )
    }
}
