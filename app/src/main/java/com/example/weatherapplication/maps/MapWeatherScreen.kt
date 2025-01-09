package com.example.weatherapplication.maps

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.rasterLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.RasterSource
import com.mapbox.maps.extension.style.sources.generated.rasterSource
import com.mapbox.maps.extension.style.sources.getSourceAs
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

// Should be hidden away bad practice!
const val API_KEY = "af95979eb6688d114fe2358eed5793bd"

// LayerTypes
private val layerTypes = listOf(
    LayerType("temp_new", "Temperature Map", "?"),
    //LayerType("radar", "Global Precipitation Map", "mm/h"),
    //LayerType("precipitation_new", "Rain Map", "mm/s"),
    LayerType("PA0", "Accumulated Precipitation", "mm"),
    //LayerType("PAC0", "Convective precipitation", "mm"),
    LayerType("PR0", "Precipitation Intensity Map", "mm/s"),
    //LayerType("PA0", "Accumulated Precipitation", "mm"),
    //LayerType("PAR0", "Accumulated Precipitation - rain", "mm"),
    //LayerType("PAS0", "Accumulated Precipitation - snow", "mm"),
    LayerType("SD0", "Snow Depth Map", "m"),
    //LayerType("WS10", "Wind speed at an altitude of 10 meters", "m/s"),
    // Joint display of speed wind (color) and wind direction (arrows), received by U and V components
    //LayerType("WND", "Avergae Wind Speed", "m/s"),
    //LayerType("APM", "Atmospheric pressure on mean sea level", "hPa"),
    //LayerType("TA2", "Air temperature at a height of 2 meters", "°C"),
    LayerType("TD2", "Temperature of a Dew Point", "°C"),
    //LayerType("TS0", "Soil temperature 0-10 cm", "K"),
    //LayerType("TS10", "Soil temperature >10 cm", "K"),
    LayerType("HRD0", "Relative Humidity", "%"),
    LayerType("CL", "Cloudiness", "%")
)

@Composable
fun MapWeatherScreen2(userLatitude: Double, userLongitude: Double, isDarkTheme: Boolean) {
    // Setting Map style
    val styleUrl = if (isDarkTheme) {
        "mapbox://styles/cosmo1024/cm5h2ah7a000d01s38y6x8ksa"
    } else {
        "mapbox://styles/cosmo1024/cm5gyxcab000601s742zx3nhg"
    }

    // Location
    var lat by remember { mutableDoubleStateOf(userLatitude) }
    var lon by remember { mutableDoubleStateOf(userLongitude) }

    // MapView
    var mapView by remember { mutableStateOf<MapView?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Slider data
    var currentTimeUnix by rememberSaveable { mutableLongStateOf(System.currentTimeMillis() / 1000L) }
    var selectedUnixTime by rememberSaveable { mutableStateOf(currentTimeUnix) }

    //
    // Fetch the current Unix time on Launched Effect
    LaunchedEffect(Unit) {
        currentTimeUnix = System.currentTimeMillis() / 1000L
    }

    //
    // Create and initialise the default layer type
    var selectedItem by rememberSaveable { mutableStateOf<LayerType>(layerTypes[0]) }

    //
    // Function to update weather style layer according to selected item type
    fun updateWeatherMapStyle() {
        mapView?.mapboxMap?.getStyle { style ->
            when (selectedItem.code) {
                "temp_new" ->
                    addOrUpdateRasterSourceAndLayer(
                        style,
                        API_KEY,
                        "temp_new",
                        selectedUnixTime)

                "precipitation_new" ->
                    addOrUpdateRasterSourceAndLayer(
                        style,
                        API_KEY,
                        "precipitation_new",
                        selectedUnixTime
                    )

                "PR0" ->
                    addOrUpdateRasterSourceAndLayer3(
                        style,
                        API_KEY,
                        "PR0",
                        selectedUnixTime
                    )

                else -> {
                    addOrUpdateRasterSourceAndLayer2(
                        style,
                        API_KEY,
                        selectedItem.code,
                        selectedUnixTime,
                        "0.8"
                    )
                }
            }
        }

        Log.d("MapView", "Updated Layer to {$selectedItem.code}")
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.padding(16.dp)
        ) {
            //
            // Dropdown Component
            DropdownWeatherComponent(
                selectedItem = selectedItem,
                onItemSelected = { item ->
                    selectedItem = item
                    Log.d("SelectedItem", "Item $selectedItem")

                    updateWeatherMapStyle()
                }
            )
        }

        Box(

        ) {
            //
            // Weather Slider
            WeatherMapSlider(
                initialSliderValue = 0.0f,
                currentTimeUnix = currentTimeUnix,
                onSelectedUnixTimeChange = { newUnixTime ->
                    selectedUnixTime = newUnixTime
                    Log.d("UnixTime", "Time $selectedUnixTime")

                    updateWeatherMapStyle()
                },
            )
        }

        //
        // Rendering the MapView
        AndroidView(
            modifier = Modifier.weight(1f),

            //
            // On initializing
            factory = { context ->
                MapView(context).apply {
                    mapboxMap.apply {
                        loadStyleUri(styleUrl)
                    }
                    mapboxMap.apply {
                        setCamera(
                            CameraOptions.Builder()
                                .center(Point.fromLngLat(lon, lat))
                                .zoom(4.5)
                                .build()
                        )
                    }
                    mapView = this

                    updateWeatherMapStyle()
                }
            },

            //
            // Continuous update
            update = { view ->
                coroutineScope.launch(Dispatchers.Main) {
                    view.location.updateSettings {
                        locationPuck = createDefault2DPuck(withBearing = false)
                        puckBearingEnabled = true
                        puckBearing = PuckBearing.HEADING
                        enabled = true
                    }
                }
            }
        )

        //
        // Get the selected item code and update the view
        Column(
            modifier = Modifier.padding(4.dp)
        ) {
            when (selectedItem.code) {
                "temp_new" ->
                    TemperatureGradientBox()
                "PR0" ->
                    PrecipitationGradientBox()
                else -> {
                    GradientBox()
                    Log.d("GradientBox", "None available")
                }
            }

            Text(
                text = "Data provided by openweathermap.org",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}

/**
 *
 * Dropdown Weather Component
 */
@Composable
fun DropdownWeatherComponent(
    selectedItem: LayerType,
    onItemSelected: (LayerType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    TextField(
        value = selectedItem.description,
        onValueChange = { /* Do nothing here */ },
        label = { Text(selectedItem.description) },
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = null
                )
            }
        }
    )
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },

    ) {
        layerTypes.forEach { item ->
            DropdownMenuItem(
                onClick = {
                    onItemSelected(item)
                    expanded = false
                },
                text = {
                    Text(item.description)
                }
            )
        }
    }
}

// v1 API implementation
private fun addOrUpdateRasterSourceAndLayer(
    style: Style,
    apiKey: String,
    layer: String,
    unixTime: Long
) {
    val sourceId = "weatherSource"
    val layerId = "weatherLayer"
    val tileUrl = "https://tile.openweathermap.org/map/$layer/{z}/{x}/{y}.png?appid=$apiKey&date=$unixTime"

    tileGetterService(style, sourceId, layerId, tileUrl)
}

// v2 API implementation
private fun addOrUpdateRasterSourceAndLayer2(
    style: Style,
    apiKey: String,
    layer: String,
    unixTime: Long,
    opacity: String = "0.7"
) {
    val sourceId = "weatherSource"
    val layerId = "weatherLayer"
    var tileUrl = "https://maps.openweathermap.org/maps/2.0/weather/$layer/{z}/{x}/{y}?appid=$apiKey&date=$unixTime&opacity=$opacity"

    tileGetterService(style, sourceId, layerId, tileUrl)
}

// v3 Radar API implementation
private fun addOrUpdateRasterSourceAndLayer3(
    style: Style,
    apiKey: String,
    layer: String,
    unixTime: Long
) {
    val sourceId = "weatherSource"
    val layerId = "weatherLayer"
    val tileUrl = "https://maps.openweathermap.org/maps/2.0/weather/$layer/{z}/{x}/{y}?appid=$apiKey&date=$unixTime&pallete=0.000005:CAEFFF; 0.000009:A8E7F7; 0.000014:7DD5F5; 0.000023:54C5F5; 0.000046:3388B0; 0.000092:22557F; 0.000231:11364E; 0.000463:3A99F2; 0.000694:2D4FE9; 0.000926:2647EB; 0.001388:1823B0; 0.002315:131D97; 0.023150:08080A"

    tileGetterService(style, sourceId, layerId, tileUrl)
}

/**
 *
 * Get the tiles and apply it to the MapView
 */
private fun tileGetterService(
    style: Style,
    sourceId: String,
    layerId: String,
    tileUrl: String
) {
    if (style.styleSourceExists(sourceId)) {
        // Update the source URL if the source already exists
        val source = style.getSourceAs<RasterSource>(sourceId)
        source?.tiles(listOf(tileUrl))
    } else {
        // Add the source if it doesn't exist
        val source = rasterSource(sourceId) {
            tileSize(512)
            tiles(listOf(tileUrl))
        }
        style.addSource(source)
    }

    if (!style.styleLayerExists(layerId)) {
        // Add the layer if it doesn't exist
        val tile = rasterLayer(layerId, sourceId) {
            minZoom(1.0)
            maxZoom(20.0)
        }
        style.addLayer(tile)
    }
}

/**
 *
 * Format Unix Time for HTTP GET Request
 */
public fun formatUnixTime(unixTime: Long): String {
    return try {
        val dateTime = LocalDateTime.ofEpochSecond(unixTime, 0, ZoneOffset.UTC)
        val outputFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy hh:mm a")
        dateTime.format(outputFormatter)
    } catch (e: DateTimeParseException) {
        unixTime.toString()
    }
}