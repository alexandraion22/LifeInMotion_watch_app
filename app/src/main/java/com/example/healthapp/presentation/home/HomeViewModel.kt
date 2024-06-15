package com.example.healthapp.presentation.home

import android.Manifest
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthapp.data.HealthServicesRepository
import com.example.healthapp.data.ServiceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val healthServicesRepository: HealthServicesRepository
) : ViewModel() {

    val uiState: StateFlow<HomeScreenState> = healthServicesRepository.serviceState.map {
        val isTrackingInAnotherApp = healthServicesRepository.isTrackingExerciseInAnotherApp()
        val hasExerciseCapabilities = healthServicesRepository.hasExerciseCapability()
        toUiState(
            serviceState = it,
            isTrackingInAnotherApp = isTrackingInAnotherApp,
            hasExerciseCapabilities = hasExerciseCapabilities
        )
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5.seconds),
        initialValue = toUiState(healthServicesRepository.serviceState.value)
    )

    fun prepareExercise(exerciseType: String) {
        viewModelScope.launch {
            healthServicesRepository.prepareExercise(exerciseType)
        }
    }

    fun getExerciseType(): String {
        return healthServicesRepository.getExerciseType()
    }
    private fun toUiState(
        serviceState: ServiceState,
        isTrackingInAnotherApp: Boolean = false,
        hasExerciseCapabilities: Boolean = true
    ): HomeScreenState {
        return if (serviceState is ServiceState.Disconnected) {
            HomeScreenState.Disconnected(serviceState, isTrackingInAnotherApp, permissions)
        } else {
            HomeScreenState.Home(
                serviceState = serviceState as ServiceState.Connected,
                isTrackingInAnotherApp = isTrackingInAnotherApp,
                requiredPermissions = permissions,
                hasExerciseCapabilities = hasExerciseCapabilities,
            )
        }
    }

    companion object {
        val permissions = mutableListOf(
            Manifest.permission.BODY_SENSORS,
            Manifest.permission.ACTIVITY_RECOGNITION,
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                this.add(Manifest.permission.POST_NOTIFICATIONS)

            this.toList()
        }
    }
}