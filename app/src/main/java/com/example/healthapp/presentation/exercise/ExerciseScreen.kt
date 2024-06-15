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

package com.example.healthapp.presentation.exercise

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.example.healthapp.presentation.component.CaloriesText
import com.example.healthapp.presentation.component.HRText
import com.example.healthapp.presentation.component.PauseButton
import com.example.healthapp.presentation.component.ResumeButton
import com.example.healthapp.presentation.component.StopButton
import com.example.healthapp.presentation.summary.SummaryScreenState
import com.example.healthapp.R
import com.example.healthapp.app.Workout
import com.example.healthapp.presentation.component.formatElapsedTime
import com.example.healthapp.presentation.theme.PsychedelicPurple
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.ambient.AmbientState
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.AlertDialog
import com.google.android.horologist.health.composables.ActiveDurationText

@Composable
fun ExerciseRoute(
    ambientState: AmbientState,
    modifier: Modifier = Modifier,
    onSummary: (SummaryScreenState) -> Unit,
    onRestart: () -> Unit,
    onFinishActivity: () -> Unit,
    onFinishExercise: (Workout) -> Unit
) {
    val viewModel = hiltViewModel<ExerciseViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (viewModel.isEnded()) {
        SideEffect {
            onSummary(uiState.toSummary())
        }
    }

    if (uiState.error != null) {
        ErrorStartingExerciseScreen(
            onRestart = onRestart,
            onFinishActivity = onFinishActivity,
            uiState = uiState
        )
    } else if (ambientState is AmbientState.Interactive) {
        ExerciseScreen(
            onPauseClick = { viewModel.pauseExercise() },
            onEndClick = { try {viewModel.endExercise(uiState.toSummary(),onFinishExercise)} catch(_: Exception)  { }},
            onResumeClick = { viewModel.resumeExercise() },
            uiState = uiState,
            modifier = modifier,
            exerciseViewModel = viewModel
        )
    }
}

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun ErrorStartingExerciseScreen(
    onRestart: () -> Unit,
    onFinishActivity: () -> Unit,
    uiState: ExerciseScreenState
) {
    AlertDialog(
        title = stringResource(id = R.string.error_starting_exercise),
        message = "${uiState.error ?: stringResource(id = R.string.unknown_error)}. ${
            stringResource(
                id = R.string.try_again
            )
        }",
        onCancel = onFinishActivity,
        onOk = onRestart,
        showDialog = true,
    )
}

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun ExerciseScreen(
    onPauseClick: () -> Unit,
    onEndClick: () -> Unit,
    onResumeClick: () -> Unit,
    uiState: ExerciseScreenState,
    modifier: Modifier = Modifier,
    exerciseViewModel : ExerciseViewModel
) {
    val columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ScalingLazyColumnDefaults.ItemType.Text,
            last = ScalingLazyColumnDefaults.ItemType.Chip
        )
    )
    ScreenScaffold(scrollState = columnState) {
        ScalingLazyColumn(
            modifier = modifier.fillMaxSize(),
            columnState = columnState
        ) {
            item {
                DurationRow(uiState, exerciseViewModel)
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Text(
                    text = "Current HR: ${uiState.exerciseState?.exerciseMetrics?.heartRate ?: "--"}",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Text(
                    text = "Max HR: ${uiState.maxHeartRate ?: "--"}",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_burn),
                        tint = PsychedelicPurple,
                        contentDescription = stringResource(id = R.string.calories),
                        modifier = Modifier.size(30.dp)
                    )
                    CaloriesText(
                        uiState.exerciseState?.exerciseMetrics?.calories
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            item {
                ExerciseControlButtons(
                    uiState,
                    onEndClick,
                    onResumeClick,
                    onPauseClick
                )
            }
        }
    }
}

@Composable
private fun ExerciseControlButtons(
    uiState: ExerciseScreenState,
    onEndClick: () -> Unit,
    onResumeClick: () -> Unit,
    onPauseClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StopButton(onEndClick)
        if (uiState.isPaused) {
            ResumeButton(onResumeClick)
        } else {
            PauseButton(onPauseClick)
        }
    }
}

@Composable
private fun DurationRow(uiState: ExerciseScreenState, exerciseViewModel: ExerciseViewModel) {
    val lastActiveDurationCheckpoint = uiState.exerciseState?.activeDurationCheckpoint
    val exerciseState = uiState.exerciseState?.exerciseState
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (exerciseState != null && lastActiveDurationCheckpoint != null) {
            ActiveDurationText(
                checkpoint = lastActiveDurationCheckpoint,
                state = uiState.exerciseState.exerciseState
            ) {
                exerciseViewModel.updateTime(it)
                Text(text = formatElapsedTime(it, includeSeconds = true), fontSize = 18.sp)
            }
        } else {
            Text(text = "--", fontSize = 18.sp)
        }
    }
}