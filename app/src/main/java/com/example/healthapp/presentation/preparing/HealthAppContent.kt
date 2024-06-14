package com.example.healthapp.presentation.preparing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HealthAppContent(onStartSensors: () -> Unit) {
        Box(
            modifier = Modifier
                .fillMaxSize(), // Make the Box fill the entire screen
            contentAlignment = Alignment.Center // Center the content of the Box
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp) // Space between buttons
            ) {
                Button(
                    onClick = { onStartSensors() }
                ) {
                    Text("Start Sensors")
                }

                Button(
                    onClick = { }
                ) {
                    Text("Stop Sensors")
                }
            }
        }
    }