package com.example.weatherapplication.maps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayerAbove
import com.mapbox.maps.extension.style.sources.addSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MapWeatherScreen(userLatitude = 0.0, userLongitude = 0.0) // Default values for demonstration
        }
    }
}

@Composable
fun MapWeatherScreen(userLatitude: Double, userLongitude: Double) {
    var mapView by remember { mutableStateOf<MapView?>(null) }
    val coroutineScope = rememberCoroutineScope()

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            MapView(context).apply {
                getMapboxMap().apply {
                    setCamera(
                        com.mapbox.maps.CameraOptions.Builder()
                            .center(com.mapbox.geojson.Point.fromLngLat(userLongitude, userLatitude))
                            .zoom(5.0)
                            .build()
                    )
                    loadStyleUri("mapbox://styles/mapbox/streets-v11")
                }
                mapView = this
            }
        },
        update = { view ->
            mapView = view
            coroutineScope.launch(Dispatchers.IO) {
                val apiKey = "be72f76237db"
                val timeApiUrl =
                    "https://maps-api.meteoblue.com/v1/tiles/precipitation_global/meteomapsTimeLast24h.json?lang=en&apikey=$apiKey"
                val timeInfo = fetchTimeInfo(timeApiUrl)
                val currentTimeStep = timeInfo?.getJSONArray("timesteps")
                    ?.getJSONObject(0)?.getString("value")

                currentTimeStep?.let {
                    view.getMapboxMap().getStyle { style ->
                        addRasterSourceAndLayer(style, apiKey, it)
                    }
                }
            }
        }
    )
}

private fun fetchTimeInfo(url: String): JSONObject? {
    val client = OkHttpClient()
    val request = Request.Builder().url(url).build()
    client.newCall(request).execute().use { response ->
        return if (response.isSuccessful) {
            response.body?.string()?.let { JSONObject(it) }
        } else {
            null
        }
    }
}

private fun addRasterSourceAndLayer(style: Style, apiKey: String, timeStep: String) {
    val source = com.mapbox.maps.extension.style.sources.generated.rasterSource("radarSource") {
        tileSize(512)
        tiles(listOf("https://maps-api.meteoblue.com/v1/tiles/precipitation_global/$timeStep/{z}/{x}/{y}.png?apikey=$apiKey"))
    }

    val layer = com.mapbox.maps.extension.style.layers.generated.rasterLayer("radarLayer", "radarSource") {
        minZoom(1.0)
        maxZoom(6.0)
    }

    style.addSource(source)
    style.addLayerAbove(layer, "building")
}