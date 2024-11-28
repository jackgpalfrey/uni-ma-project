package com.example.weatherapplication.maps

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.weatherapplication.userLatitude
import com.example.weatherapplication.userLongitude
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState

@Composable
fun SimpleMap() {
    MapboxMap(
        Modifier.fillMaxSize(),
        mapViewportState = rememberMapViewportState {
            setCameraOptions {
                zoom(10.0)
                center(userLongitude?.let { userLatitude?.let { it1 -> Point.fromLngLat(it, it1) } })
                pitch(0.0)
                bearing(0.0)
            }
        },
    )
}