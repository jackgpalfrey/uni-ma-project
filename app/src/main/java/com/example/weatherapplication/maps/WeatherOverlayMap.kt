package com.example.weatherapplication.maps

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mapbox.geojson.Point
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.style.LongValue
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.extension.compose.style.PointListValue
import com.mapbox.maps.extension.compose.style.StringListValue
import com.mapbox.maps.extension.compose.style.sources.generated.rememberImageSourceState
import com.mapbox.maps.extension.compose.style.sources.generated.rememberRasterSourceState

/**
 * Renders a Mapbox map with satellite imagery and overlays
 * OpenWeatherMap precipitation tiles using RasterSource + RasterLayer.
 */
@Composable
fun WeatherOverlayMap(
    lat: Double,
    lon: Double
) {
    // Remember map viewport (camera position)
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(Point.fromLngLat(lon, lat))
            zoom(8.0)
        }
    }

    val rasterSource = rememberRasterSourceState() {
        tileSize = LongValue(256)
        tiles = StringListValue(listOf(""))
    }

    val imageSource = rememberImageSourceState() {
        coordinates = PointListValue(
            listOf(
                listOf(-80.11725, 25.7836),
                listOf(-80.1397431334, 25.783548),
                listOf(-80.13964, 25.7680),
                listOf(-80.11725, 25.76795)
            )
        )
    }



    // Render the Mapbox map with a satellite style
    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = mapViewportState,
        style = {
            MapStyle(style = Style.STANDARD_SATELLITE)
        }
    ) {
        // If you want additional UI (e.g., date/time selectors), add it here
    }
}
