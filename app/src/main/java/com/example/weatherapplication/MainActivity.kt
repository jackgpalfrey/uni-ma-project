package com.example.weatherapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.weatherapplication.api.RetrofitClient
import com.example.weatherapplication.api.WeatherResponse
import com.example.weatherapplication.location.UserLocationProvider
import com.example.weatherapplication.nav.BottomNavigationBar
import com.example.weatherapplication.ui.theme.MyCustomTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

sealed class Views(val route : String) {
    data object Home : Views("home_route")
    data object Map : Views("map_route")
    data object Forecast : Views("forecast_route")
}

class MainActivity : ComponentActivity() {
    private val locationPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission granted, access location
                getUserLocation()
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Location permission denied...", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set Screen content
        setContent {
            MyCustomTheme {
                MainScreen()
            }
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun getUserLocation() {
        // Initialize and use UserLocationProvider
        val userLocationProvider = UserLocationProvider(this)
        val latitude = userLocationProvider.latitude
        val longitude = userLocationProvider.longitude

        if (latitude != null && longitude != null) {
            Toast.makeText(this, "Lat: $latitude, Long: $longitude", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Unable to fetch location...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchWeather(lat: Double, lon: Double) {
        val call = RetrofitClient.weatherService.getCurrentWeather(lat, lon, "f129e0a6aafbd7ec0c71ed808a7b7b53")

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    weatherResponse?.let {
                        println("Weather: ${it.weather[0].description}, Temp: ${it.main.temp}")
                    }
                } else {
                    println("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                println("Failed to fetch weather: ${t.message}")
            }
        })
    }
}

@Composable
fun MainScreen() {
    // A surface container using the 'background' color from the theme
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        BottomNavigationBar()
    }
}