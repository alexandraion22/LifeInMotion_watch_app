/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.healthapp.presentation.exercise

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthapp.app.Workout
import com.example.healthapp.data.HealthServicesRepository
import com.example.healthapp.data.ServiceState
import com.example.healthapp.presentation.summary.SummaryScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.Duration

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val healthServicesRepository: HealthServicesRepository
) : ViewModel() {
    private var maxHeartRate: Double? = null
    private var minHeartRate: Double? = null

    val uiState: StateFlow<ExerciseScreenState> = healthServicesRepository.serviceState.map {
        val exerciseState = (it as? ServiceState.Connected)?.exerciseServiceState
        updateHeartRateBounds(exerciseState?.exerciseMetrics?.heartRate)
        ExerciseScreenState(
            hasExerciseCapabilities = healthServicesRepository.hasExerciseCapability(),
            isTrackingAnotherExercise = healthServicesRepository.isTrackingExerciseInAnotherApp(),
            serviceState = it,
            exerciseState = exerciseState,
            maxHeartRate = maxHeartRate,
            minHeartRate = minHeartRate,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(3_000),
        healthServicesRepository.serviceState.value.let {
            ExerciseScreenState(
                true,
                false,
                it,
                (it as? ServiceState.Connected)?.exerciseServiceState,
                maxHeartRate = null,  // Initial values
                minHeartRate = null,  // Initial values
            )
        }
    )

    fun updateTime(elapsedTime : Duration){
        healthServicesRepository.updateTime(elapsedTime)
    }
    private fun updateHeartRateBounds(currentHeartRate: Double?) {
        currentHeartRate?.let {
            if (maxHeartRate == null || it > maxHeartRate!!) {
                maxHeartRate = it
            }
            if(it.toInt() !=0)
                if (minHeartRate == null || it < minHeartRate!!) {
                    minHeartRate = it
                }
        }
    }

    suspend fun isExerciseInProgress(): Boolean {
        return healthServicesRepository.isExerciseInProgress()
    }

    fun startExercise(exerciseType: String) {
        healthServicesRepository.startExercise(exerciseType)
    }

    fun pauseExercise() {
        healthServicesRepository.pauseExercise()
    }

    fun endExercise(summary: SummaryScreenState, onFinishExercise: (Workout) -> Unit) {
        Log.e("TAG",summary.toString())
        onFinishExercise(Workout(avgHR = summary.averageHeartRate.toInt(),
            minHR =summary.minHeartRate.toInt(),
            maxHR = summary.maxHeartRate.toInt(),
            calories = summary.totalCalories.toInt(),
            duration = healthServicesRepository.getElapsedTime().toMillis(),
            exerciseType = healthServicesRepository.getExerciseType()))
        healthServicesRepository.endExercise()
    }

    fun resumeExercise() {
        healthServicesRepository.resumeExercise()
    }

    fun isEnded(): Boolean {
        return healthServicesRepository.isEnded()
    }

    fun updateIsEnded(b: Boolean) {
        healthServicesRepository.updateIsEnded(b)
    }

    fun getElapsedTime(): Duration {
        return healthServicesRepository.getElapsedTime()
    }
}



