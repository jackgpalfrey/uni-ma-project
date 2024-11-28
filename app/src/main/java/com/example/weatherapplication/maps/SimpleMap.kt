package com.example.weatherapplication.maps

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState

@Composable
fun SimpleMap(lat: Double, lon: Double) {
    MapboxMap(
        Modifier.fillMaxSize(),
        mapViewportState = rememberMapViewportState {
            setCameraOptions {
                zoom(10.0)
                center(Point.fromLngLat(lat, lon))
                pitch(0.0)
                bearing(0.0)
            }
        },
    )
}