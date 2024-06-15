package com.example.healthapp.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ChipDefaults
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.material.Chip

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun BackgroundServicesContent(onStartSensors: () -> Unit, onStopSensors: () -> Unit) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f),
            contentAlignment = Alignment.Center
        ) {
            Column(
            ) {
                Chip(
                    label = "Start sensors",
                    colors = ChipDefaults.chipColors(backgroundColor = Color(0XFF20A072)),
                    onClick = onStartSensors
                )
                Spacer(modifier = Modifier.height(8.dp))
                Chip(
                    colors = ChipDefaults.chipColors(backgroundColor = Color(0XFFC4311D)),
                    label = "Stop sensors",
                    onClick = onStopSensors,
                )
            }
        }
    }