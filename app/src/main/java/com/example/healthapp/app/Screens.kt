package com.example.healthapp.app

import androidx.navigation.NavController
import com.example.healthapp.presentation.summary.SummaryScreenState

sealed class Screen(
    val route: String
) {
    object Exercise : Screen("exercise")
    object ExerciseNotAvailable : Screen("exerciseNotAvailable")
    object Home : Screen("preparingExercise")
    object Summary : Screen("summaryScreen") {
        fun buildRoute(summary: SummaryScreenState): String {
            return "$route/${summary.averageHeartRate}/${summary.totalDistance}/${summary.totalCalories}/${summary.elapsedTime}/${summary.maxHeartRate}/${summary.minHeartRate}"
        }

        val averageHeartRateArg = "averageHeartRate"
        val totalDistanceArg = "totalDistance"
        val totalCaloriesArg = "totalCalories"
        val elapsedTimeArg = "elapsedTime"
        val maxHeartRateArg = "maxHeartRate"
        val minHeartRateArg = "minHeartRate"
    }
}

fun NavController.navigateToTopLevel(screen: Screen, route: String = screen.route) {
    navigate(route) {
        popUpTo(graph.id) {
            inclusive = true
        }
    }
}