package com.example.weatherapplication.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.weatherapplication.maps.SimpleMap

@Composable
fun MapScreen(navController: NavController) {
    SimpleMap()
}