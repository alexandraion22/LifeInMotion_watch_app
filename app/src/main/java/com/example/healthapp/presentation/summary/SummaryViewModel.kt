package com.example.healthapp.presentation.summary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.healthapp.app.Screen
import com.example.healthapp.presentation.summary.SummaryScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.Duration
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val uiState = MutableStateFlow(
        SummaryScreenState(
            averageHeartRate = savedStateHandle.get<Float>(Screen.Summary.averageHeartRateArg)!!
                .toDouble(),
            totalDistance = savedStateHandle.get<Float>(Screen.Summary.totalDistanceArg)!!
                .toDouble(),
            totalCalories = savedStateHandle.get<Float>(Screen.Summary.totalCaloriesArg)!!
                .toDouble(),
            elapsedTime = Duration.parse(savedStateHandle.get(Screen.Summary.elapsedTimeArg)!!),
            maxHeartRate = savedStateHandle.get<Float>(Screen.Summary.maxHeartRateArg)?.toDouble() ?: Double.NaN,
            minHeartRate = savedStateHandle.get<Float>(Screen.Summary.minHeartRateArg)?.toDouble() ?: Double.NaN
        )
    )
}