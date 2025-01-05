package com.example.weatherapplication.maps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TemperatureGradientBox() {
    // Define the gradient colors
    val gradientColors = listOf(
        Color(0xFF9F55B5), // rgb(159, 85, 181)
        Color(0xFF2C6ABB), // rgb(44, 106, 187)
        Color(0xFF528BD5), // rgb(82, 139, 213)
        Color(0xFF67A3DE), // rgb(103, 163, 222)
        Color(0xFF8ECAF0), // rgb(142, 202, 240)
        Color(0xFF9BD5F4), // rgb(155, 213, 244)
        Color(0xFFACE1FD), // rgb(172, 225, 253)
        Color(0xFFC2EAFF), // rgb(194, 234, 255)
        Color(0xFFFFFFD0), // rgb(255, 255, 208)
        Color(0xFFFEF8AE), // rgb(254, 248, 174)
        Color(0xFFFEE892), // rgb(254, 232, 146)
        Color(0xFFFEE270), // rgb(254, 226, 112)
        Color(0xFFFDD461), // rgb(253, 212, 97)
        Color(0xFFF4A85E), // rgb(244, 168, 94)
        Color(0xFFF48159), // rgb(244, 129, 89)
        Color(0xFFF46859), // rgb(244, 104, 89)
        Color(0xFFF44C49)  // rgb(244, 76, 73)
    )

    // Create the linear gradient brush
    val gradientBrush = Brush.linearGradient(
        colors = gradientColors
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Gradient Box
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(20.dp)
                    .background(gradientBrush)
            )
        }

        // Temperature Scale
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            val temperatures = listOf("-40", "-20", "0", "20", "40")
            temperatures.forEach { temperature ->
                Text(
                    text = temperature,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 12.sp
                )
            }
        }

        // Accompanying Text
        Text(
            text = "Temperature",
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TemperatureGradientBoxPreview() {
    TemperatureGradientBox()
}