package com.example.weatherapplication.maps

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState

@Composable
fun SimpleMap(lat: Double, lon: Double) {
    Log.d("SimpleMap", "Lat: $lat, Long: $lon")

    MapboxMap(
        Modifier.fillMaxSize(),
        mapViewportState = rememberMapViewportState {
            setCameraOptions {
                zoom(3.0)
                // Corrected: Longitude (lon) should come first, Latitude (lat) second
                center(Point.fromLngLat(lon, lat))
                pitch(0.0)
                bearing(0.0)
            }
        },
    )
}