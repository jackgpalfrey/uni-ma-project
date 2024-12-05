package com.example.weatherapplication.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun ForecastScreen() {
    var userInput by remember { mutableStateOf(TextFieldValue("")) } // For capturing input
    var response by remember { mutableStateOf("Computer: I think I need a byte to eat.") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "You: \"Computer, why are you so slow today?\"")
        Text(text = response)

        Text(text = "You: \"What would you like to eat?\"")
        BasicTextField(
            value = userInput,
            onValueChange = { userInput = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        Button(
            onClick = {
                // Simulate a response from the computer
                response = "Computer: *accepts ${userInput.text}* \"Nom nom... By the way, I deleted all your cookies for you.\""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit")
        }
    }
}
