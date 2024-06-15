package com.example.healthapp.presentation.home
import com.example.healthapp.data.ServiceState

sealed class HomeScreenState {
    abstract val serviceState: ServiceState
    abstract val isTrackingInAnotherApp: Boolean
    abstract val requiredPermissions: List<String>

    data class Disconnected(
        override val serviceState: ServiceState.Disconnected,
        override val isTrackingInAnotherApp: Boolean,
        override val requiredPermissions: List<String>
    ) : HomeScreenState()

    data class Home(
        override val serviceState: ServiceState.Connected,
        override val isTrackingInAnotherApp: Boolean,
        override val requiredPermissions: List<String>,
        val hasExerciseCapabilities: Boolean
    ) : HomeScreenState()
}