package com.example.weatherapplication.maps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayerAbove
import com.mapbox.maps.extension.style.layers.generated.rasterLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.rasterSource
import com.mapbox.maps.extension.style.sources.getSourceAs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MapWeatherScreen(userLatitude = 0.0, userLongitude = 7.0)
        }
    }
}

@Composable
fun MapWeatherScreen(userLatitude: Double, userLongitude: Double) {
    var mapView by remember { mutableStateOf<MapView?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var timeSteps by remember { mutableStateOf<List<String>>(emptyList()) }
    var currentTimeStepIndex by rememberSaveable { mutableIntStateOf(0) }

    // Fetch time steps on initial launch
    LaunchedEffect(Unit) {
        val apiKey = "be72f76237db"
        val timeApiUrl =
            "https://maps-api.meteoblue.com/v1/tiles/precipitation_global/meteomapsTimeLast24h.json?lang=en&apikey=$apiKey"

        // Fetch time steps in a background thread
        coroutineScope.launch(Dispatchers.IO) {
            val fetchedTimeSteps = fetchTimeInfo(timeApiUrl)
            fetchedTimeSteps?.let {
                timeSteps = it
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.weight(1f),
            factory = { context ->
                MapView(context).apply {
                    mapboxMap.apply {
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
                    if (timeSteps.isNotEmpty()) {
                        val currentTimeStep = timeSteps[currentTimeStepIndex]
                        view.mapboxMap.getStyle { style ->
                            addOrUpdateRasterSourceAndLayer(style, apiKey, currentTimeStep)
                        }
                    }
                }
            }
        )

        if (timeSteps.isNotEmpty()) {
            Slider(
                value = currentTimeStepIndex.toFloat(),
                onValueChange = { currentTimeStepIndex = it.toInt() },
                valueRange = 0f..(timeSteps.size - 1).toFloat(),
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Time Step: ${timeSteps[currentTimeStepIndex]}",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

private fun fetchTimeInfo(url: String): List<String>? {
    val client = OkHttpClient()
    val request = Request.Builder().url(url).build()
    client.newCall(request).execute().use { response ->
        return if (response.isSuccessful) {
            response.body?.string()?.let {
                val json = JSONObject(it)
                json.getJSONArray("timesteps").let { timesteps ->
                    List(timesteps.length()) { index ->
                        timesteps.getJSONObject(index).getString("value")
                    }
                }
            }
        } else {
            null
        }
    }
}

private fun addOrUpdateRasterSourceAndLayer(style: Style, apiKey: String, timeStep: String) {
    val sourceId = "radarSource"
    val layerId = "radarLayer"

    if (style.styleSourceExists(sourceId)) {
        // Update the source URL if the source already exists
        val source = style.getSourceAs<com.mapbox.maps.extension.style.sources.generated.RasterSource>(sourceId)
        source?.tiles(listOf("https://maps-api.meteoblue.com/v1/tiles/precipitation_global/$timeStep/{z}/{x}/{y}.png?apikey=$apiKey"))
    } else {
        // Add the source if it doesn't exist
        val source = rasterSource(sourceId) {
            tileSize(512)
            tiles(listOf("https://maps-api.meteoblue.com/v1/tiles/precipitation_global/$timeStep/{z}/{x}/{y}.png?apikey=$apiKey"))
        }
        style.addSource(source)
    }

    if (!style.styleLayerExists(layerId)) {
        // Add the layer if it doesn't exist
        val layer = rasterLayer(layerId, sourceId) {
            minZoom(1.0)
            maxZoom(6.0)
        }
        style.addLayerAbove(layer, "building")
    }
}