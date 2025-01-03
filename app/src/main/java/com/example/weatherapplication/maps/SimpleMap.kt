package com.example.weatherapplication.maps

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mapbox.geojson.Point
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.style.MapStyle

@Composable
fun SimpleMap(lat: Double, lon: Double) {
    Log.d("SimpleMap", "Lat: $lat, Lon: $lon")

    // Remember map viewport state
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(7.0)
            center(Point.fromLngLat(lon, lat))
            pitch(0.0)
            bearing(0.0)
        }
    }

    // Render MapboxMap
    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = mapViewportState,
        style = {
            MapStyle(style = Style.SATELLITE)
        }
    ) {
        // Add map style, source, and layer setup in LaunchedEffect
    }
}
