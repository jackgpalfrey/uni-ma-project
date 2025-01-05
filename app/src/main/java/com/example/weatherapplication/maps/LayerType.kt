package com.example.weatherapplication.maps

import java.io.Serializable

data class LayerType(
    val code: String,
    val description: String,
    val unit: String
): Serializable