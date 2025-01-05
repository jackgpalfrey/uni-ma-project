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
        Color(0xFF9F55B5).copy(alpha = 0.6f), // rgb(159, 85, 181)
        Color(0xFF2C6ABB).copy(alpha = 0.6f), // rgb(44, 106, 187)
        Color(0xFF528BD5).copy(alpha = 0.6f), // rgb(82, 139, 213)
        Color(0xFF67A3DE).copy(alpha = 0.6f), // rgb(103, 163, 222)
        Color(0xFF8ECAF0).copy(alpha = 0.6f), // rgb(142, 202, 240)
        Color(0xFF9BD5F4).copy(alpha = 0.6f), // rgb(155, 213, 244)
        Color(0xFFACE1FD).copy(alpha = 0.6f), // rgb(172, 225, 253)
        Color(0xFFC2EAFF).copy(alpha = 0.6f), // rgb(194, 234, 255)
        Color(0xFFFFFFD0).copy(alpha = 0.6f), // rgb(255, 255, 208)
        Color(0xFFFEF8AE).copy(alpha = 0.6f), // rgb(254, 248, 174)
        Color(0xFFFEE892).copy(alpha = 0.6f), // rgb(254, 232, 146)
        Color(0xFFFEE270).copy(alpha = 0.6f), // rgb(254, 226, 112)
        Color(0xFFFDD461).copy(alpha = 0.6f), // rgb(253, 212, 97)
        Color(0xFFF4A85E).copy(alpha = 0.6f), // rgb(244, 168, 94)
        Color(0xFFF48159).copy(alpha = 0.6f), // rgb(244, 129, 89)
        Color(0xFFF46859).copy(alpha = 0.6f), // rgb(244, 104, 89)
        Color(0xFFF44C49).copy(alpha = 0.6f)  // rgb(244, 76, 73)
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

@Composable
fun PrecipitationGradientBox() {
    // Define the gradient colors
    val gradientColors = listOf(
        Color(0xFFFEF9CA).copy(alpha = 0.6f), // 0.000005: #FEF9CA
        Color(0xFFB9F7A8).copy(alpha = 0.6f), // 0.000009: #B9F7A8
        Color(0xFF93F57D).copy(alpha = 0.6f), // 0.000014: #93F57D
        Color(0xFF78F554).copy(alpha = 0.6f), // 0.000023: #78F554
        Color(0xFF50B033).copy(alpha = 0.6f), // 0.000046: #50B033
        Color(0xFF387F22).copy(alpha = 0.6f), // 0.000092: #387F22
        Color(0xFF204E11).copy(alpha = 0.6f), // 0.000231: #204E11
        Color(0xFFF2A33A).copy(alpha = 0.6f), // 0.000463: #F2A33A
        Color(0xFFE96F2D).copy(alpha = 0.6f), // 0.000694: #E96F2D
        Color(0xFFEB4726).copy(alpha = 0.6f), // 0.000926: #EB4726
        Color(0xFFB02318).copy(alpha = 0.6f), // 0.001388: #B02318
        Color(0xFF971D13).copy(alpha = 0.6f), // 0.002315: #971D13
        Color(0xFF090A08).copy(alpha = 0.6f)  // 0.023150: #090A08
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
            val rainfall = listOf("0", "0.5", "1", "2", "4", "6", "7", "10", "12", "14", "16", "24", "32", "60")
            rainfall.forEach { mm ->
                Text(
                    text = mm,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 10.sp
                )
            }
        }

        // Accompanying Text
        Text(
            text = "Precipitation mm/h",
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}