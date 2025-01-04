package com.example.weatherapplication.maps

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.weatherapplication.ui.theme.ThemeViewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.Layer
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
import java.io.Serializable
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.exp

// Should be hidden away bad practice!
const val API_KEY = "af95979eb6688d114fe2358eed5793bd"

@Composable
fun MapWeatherScreen2(userLatitude: Double, userLongitude: Double, themeViewModel: ThemeViewModel, isDarkTheme: Boolean) {
    // Location
    var lat by remember { mutableDoubleStateOf(userLatitude) }
    var lon by remember { mutableDoubleStateOf(userLongitude) }

    // MapView
    var mapView by remember { mutableStateOf<MapView?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Slider
    var currentTimeUnix by rememberSaveable { mutableLongStateOf(System.currentTimeMillis() / 1000L) }
    var selectedUnixTime by rememberSaveable { mutableStateOf(currentTimeUnix) }

    // Fetch the current Unix time and update map on initial launch
    LaunchedEffect(Unit) {
        currentTimeUnix = System.currentTimeMillis() / 1000L
    }

    // Select Default Map overlay
    var selectedItem by rememberSaveable { mutableStateOf<LayerType>(layerTypes[0]) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Dropdown Menu

        Box {
            DropdownWeatherComponent(
                selectedItem = selectedItem,
                onItemSelected = { item ->
                    selectedItem = item
                    Log.d("SelectedItem", "Item $selectedItem")
                    // ?Update
                }
            )
        }

        WeatherMapSlider(
            initialSliderValue = 0.0f,
            currentTimeUnix = currentTimeUnix,
            onSelectedUnixTimeChange = { newUnixTime ->
                selectedUnixTime = newUnixTime
                Log.d("UnixTime", "Time $selectedUnixTime")
            }
        )

        Button(
            onClick = {
                mapView?.mapboxMap?.getStyle { style ->
                    addOrUpdateRasterSourceAndLayer(style, API_KEY, selectedItem.code, selectedUnixTime)
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Update Layer")
        }

        // Setting Map style
        val styleUrl = if (isDarkTheme) {
            "mapbox://styles/cosmo1024/cm5h2ah7a000d01s38y6x8ksa"
        } else {
            "mapbox://styles/cosmo1024/cm5gyxcab000601s742zx3nhg"
        }

        AndroidView(
            modifier = Modifier.weight(1f),
            factory = { context ->
                MapView(context).apply {
                    mapboxMap.apply {
                        loadStyleUri(styleUrl)
                    }
                    mapboxMap.apply {
                        setCamera(
                            CameraOptions.Builder()
                                .center(Point.fromLngLat(lon, lat))
                                .zoom(4.0)
                                .build()
                        )
                    }
                    mapView = this
                }
            },
            update = { view ->
                coroutineScope.launch(Dispatchers.Main) {
                    view.mapboxMap.getStyle { style ->
                        addOrUpdateRasterSourceAndLayer(style, API_KEY, selectedItem.code, selectedUnixTime)
                    }

                    view.location.updateSettings {
                        locationPuck = createDefault2DPuck(withBearing = false)
                        puckBearingEnabled = true
                        puckBearing = PuckBearing.HEADING
                        enabled = true
                    }

                    /*
                     * Keep updating the camera position?
                    view.mapboxMap.apply {
                        setCamera(
                            CameraOptions.Builder()
                                .center(Point.fromLngLat(lon, lat))
                                .zoom(4.0)
                                .build()
                        )
                    }
                    */
                }
            }
        )
    }
}

val layerTypes = listOf(
    //LayerType("PAC0", "Convective precipitation", "mm"),
    LayerType("PR0", "Precipitation Intensity", "mm/s"),
    LayerType("PA0", "Accumulated Precipitation", "mm"),
    //LayerType("PAR0", "Accumulated Precipitation - rain", "mm"),
    //LayerType("PAS0", "Accumulated Precipitation - snow", "mm"),
    LayerType("SD0", "Depth of Snow", "m"),
    //LayerType("WS10", "Wind speed at an altitude of 10 meters", "m/s"),
    // Joint display of speed wind (color) and wind direction (arrows), received by U and V components
    LayerType("WND", "Avergae Wind Speed", "m/s"),
    //LayerType("APM", "Atmospheric pressure on mean sea level", "hPa"),
    //LayerType("TA2", "Air temperature at a height of 2 meters", "°C"),
    LayerType("TD2", "Temperature of a Dew Point", "°C"),
    //LayerType("TS0", "Soil temperature 0-10 cm", "K"),
    //LayerType("TS10", "Soil temperature >10 cm", "K"),
    LayerType("HRD0", "Relative Humidity", "%"),
    LayerType("CL", "Cloudiness", "%")
)

data class LayerType(
    val code: String,
    val description: String,
    val unit: String
): Serializable

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
        onDismissRequest = { expanded = false }
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

@Composable
fun WeatherMapSlider(
    initialSliderValue: Float,
    currentTimeUnix: Long,
    onSelectedUnixTimeChange: (Long) -> Unit
) {
    var currentSliderValue by rememberSaveable { mutableStateOf(initialSliderValue) }
    val totalHours = 5 * 24 // 5 days * 24 hours
    val oneHourInSeconds = 3600

    // Update selectedUnixTime based on the currentSliderValue
    LaunchedEffect(currentSliderValue) {
        val selectedUnixTime = currentTimeUnix + (currentSliderValue.toLong() * oneHourInSeconds)
        onSelectedUnixTimeChange(selectedUnixTime)
    }

    val formattedDateTime = formatUnixTime(currentTimeUnix + (currentSliderValue.toLong() * oneHourInSeconds))

    Column {
        TimeSlider(
            currentSliderValue = currentSliderValue,
            onSliderValueChange = { currentSliderValue = it },
            totalHours = totalHours,
            valueRange = -48f..72f // -2 days * 24 hours to +3 days * 24 hours
        )

        Text(
            text = "Date: $formattedDateTime",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun TimeSlider(
    currentSliderValue: Float,
    onSliderValueChange: (Float) -> Unit,
    totalHours: Int,
    valueRange: ClosedFloatingPointRange<Float>
) {
    Slider(
        value = currentSliderValue,
        onValueChange = onSliderValueChange,
        valueRange = valueRange,
        steps = totalHours - 1,
        modifier = Modifier.padding(16.dp)
    )
}

private fun addOrUpdateRasterSourceAndLayer(style: Style, apiKey: String, layer: String, unixTime: Long) {
    val sourceId = "weatherSource"
    val layerId = "weatherLayer"
    val tileUrl = "https://maps.openweathermap.org/maps/2.0/weather/$layer/{z}/{x}/{y}?appid=$apiKey&date=$unixTime&opacity=0.7"

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

private fun formatUnixTime(unixTime: Long): String {
    return try {
        val dateTime = LocalDateTime.ofEpochSecond(unixTime, 0, ZoneOffset.UTC)
        val outputFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy, hh:mm a")
        dateTime.format(outputFormatter)
    } catch (e: DateTimeParseException) {
        unixTime.toString()
    }
}