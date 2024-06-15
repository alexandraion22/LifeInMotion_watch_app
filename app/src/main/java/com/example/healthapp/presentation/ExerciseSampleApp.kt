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
@file:OptIn(ExperimentalHorologistApi::class)

package com.example.healthapp.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.currentBackStackEntryAsState
import com.example.healthapp.app.Screen.Exercise
import com.example.healthapp.app.Screen.ExerciseNotAvailable
import com.example.healthapp.app.Screen.Home
import com.example.healthapp.app.Screen.Summary
import com.example.healthapp.app.Workout
import com.example.healthapp.app.navigateToTopLevel
import com.example.healthapp.presentation.dialogs.ExerciseNotAvailable
import com.example.healthapp.presentation.exercise.ExerciseRoute
import com.example.healthapp.presentation.exercise.ExerciseViewModel
import com.example.healthapp.presentation.home.HomeRoute
import com.example.healthapp.presentation.home.HomeViewModel
import com.example.healthapp.presentation.summary.SummaryRoute
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.ambient.AmbientAware
import com.google.android.horologist.compose.ambient.AmbientState
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ResponsiveTimeText

/** Navigation for the exercise app. **/
@Composable
fun ExerciseSampleApp(
    navController: NavHostController,
    onFinishActivity: () -> Unit,
    onStartSensors: () -> Unit,
    onStopSensors: () -> Unit,
    homeViewModel: HomeViewModel,
    exerciseViewModel: ExerciseViewModel,
    onFinishExercise: (Workout) -> Unit
) {
    val currentScreen by navController.currentBackStackEntryAsState()
    val isAlwaysOnScreen = currentScreen?.destination?.route in AlwaysOnRoutes

    AmbientAware(
        isAlwaysOnScreen = isAlwaysOnScreen
    ) { ambientStateUpdate ->

        AppScaffold(
            timeText = {
                if (ambientStateUpdate.ambientState is AmbientState.Interactive) {
                    ResponsiveTimeText()
                }
            }
        ) {
            SwipeDismissableNavHost(
                navController = navController,
                startDestination = Exercise.route,

                ) {
                composable(Home.route) {
                    HomeRoute(
                        ambientState = ambientStateUpdate.ambientState,
                        onNoExerciseCapabilities = {
                            navController.navigate(ExerciseNotAvailable.route) {
                                popUpTo(navController.graph.id) {
                                    inclusive = false
                                }
                            }
                        },
                        onStartSensors = onStartSensors,
                        onStopSensors = onStopSensors,
                        onPrepareExercise = { exerciseType ->
                            homeViewModel.prepareExercise(exerciseType)
                            navController.navigate(Exercise.route)
                            exerciseViewModel.startExercise(exerciseType)
                        }
                    )
                }

                composable(Exercise.route) {
                    ExerciseRoute(
                        ambientState = ambientStateUpdate.ambientState,
                        onSummary = {
                            navController.navigateToTopLevel(Summary, Summary.buildRoute(it))
                        },
                        onRestart = {
                            navController.navigateToTopLevel(Home)
                        },
                        onFinishActivity = onFinishActivity,
                        onFinishExercise = onFinishExercise
                    )
                }

                composable(ExerciseNotAvailable.route) {
                    ExerciseNotAvailable()
                }

                composable(
                    Summary.route + "/{averageHeartRate}/{totalDistance}/{totalCalories}/{elapsedTime}/{maxHeartRate}/{minHeartRate}",
                    arguments = listOf(
                        navArgument(Summary.averageHeartRateArg) { type = NavType.FloatType },
                        navArgument(Summary.totalDistanceArg) { type = NavType.FloatType },
                        navArgument(Summary.totalCaloriesArg) { type = NavType.FloatType },
                        navArgument(Summary.elapsedTimeArg) { type = NavType.StringType },
                        navArgument(Summary.maxHeartRateArg) {type = NavType.FloatType},
                        navArgument(Summary.minHeartRateArg) {type = NavType.FloatType},
                        )
                ) {
                    SummaryRoute(
                        onRestartClick = {
                            navController.navigateToTopLevel(Home)
                        }
                    )
                }
            }
        }
    }
}

val AlwaysOnRoutes = listOf(Home.route, Exercise.route)


