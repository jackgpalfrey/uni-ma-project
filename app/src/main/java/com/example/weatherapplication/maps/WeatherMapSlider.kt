package com.example.weatherapplication.maps

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 *
 * Weather Slider
 */
@Composable
fun WeatherMapSlider(
    initialSliderValue: Float,
    currentTimeUnix: Long,
    onSelectedUnixTimeChange: (Long) -> Unit
) {
    var currentSliderValue by rememberSaveable { mutableFloatStateOf(initialSliderValue) }
    val totalHours = 4 * 24
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
            valueRange = -48f..48f // 4 days
        )
        Text(
            text = formattedDateTime,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleSmall
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
        modifier = Modifier
            .padding(16.dp, 0.dp)
            .height(18.dp)
    )
}