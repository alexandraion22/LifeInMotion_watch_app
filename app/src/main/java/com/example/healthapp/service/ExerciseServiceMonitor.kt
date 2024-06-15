package com.example.healthapp.service

import android.annotation.SuppressLint
import android.app.Service
import androidx.health.services.client.data.ExerciseUpdate
import com.example.healthapp.data.ExerciseClientManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class ExerciseServiceMonitor @Inject constructor(
    val exerciseClientManager: ExerciseClientManager,
    val service: Service
) {
    val exerciseService = service as ExerciseService

    val exerciseServiceState = MutableStateFlow(
        ExerciseServiceState(
            exerciseState = null,
            exerciseMetrics = ExerciseMetrics()
        )
    )

    suspend fun monitor() {
        exerciseClientManager.exerciseUpdateFlow.collect {
            processExerciseUpdate(it.exerciseUpdate)
        }
    }

    @SuppressLint("RestrictedApi")
    private fun processExerciseUpdate(exerciseUpdate: ExerciseUpdate) {
        // Dismiss any ongoing activity notification.
        if (exerciseUpdate.exerciseStateInfo.state.isEnded) {
            exerciseService.removeOngoingActivityNotification()
        }

        exerciseServiceState.update { old ->
            old.copy(
                exerciseState = exerciseUpdate.exerciseStateInfo.state,
                exerciseMetrics = old.exerciseMetrics.update(exerciseUpdate.latestMetrics),
                activeDurationCheckpoint = exerciseUpdate.activeDurationCheckpoint
                    ?: old.activeDurationCheckpoint
            )
        }
    }
}