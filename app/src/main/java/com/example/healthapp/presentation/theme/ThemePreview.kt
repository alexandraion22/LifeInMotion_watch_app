package com.example.healthapp.presentation.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.MaterialTheme
import com.example.healthapp.presentation.theme.ExerciseSampleTheme

@Composable
fun ThemePreview(content: @Composable () -> Unit) {
    ExerciseSampleTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            content()
        }
    }
}