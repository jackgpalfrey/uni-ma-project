package com.example.weatherapplication.common

import android.content.Context
import android.content.res.Resources
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun format24hTimeFromTimestamp(unixTime: Long): String {
    val date = Date(unixTime * 1000) // Convert seconds to milliseconds
    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
    return format.format(date)
}

fun format24hTimeFromDatetimeText(dateString: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return try {
        val date = inputFormat.parse(dateString)
        if (date != null) {
            outputFormat.format(date)
        } else {
            "Unknown"
        }
    } catch (e: Exception) {
        "Invalid"
    }
}
fun formatDayWithDateFromDateText(dateString: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormat = SimpleDateFormat("EEE d/M", Locale.getDefault())
    return try {
        val date = inputFormat.parse(dateString)
        if (date != null) {
            outputFormat.format(date)
        } else {
            "Unknown"
        }
    } catch (e: Exception) {
        "Invalid"
    }
}
fun getIconID(context: Context, icon: String): Int{
    val res: Resources = context.resources;
    val id = res.getIdentifier("weather"+icon, "drawable", context.packageName)
    return id
}

fun mpsToMph(metersPerSecond: Double?): Double{
    val conversionMultiplier = 2.237

    var value: Double = 0.0
    if (metersPerSecond != null) {
        value = metersPerSecond
    }

    return value * conversionMultiplier
}
