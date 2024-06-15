@file:OptIn(ExperimentalHorologistApi::class)
package com.example.healthapp.presentation.summary

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material.ChipDefaults
import com.example.healthapp.presentation.component.SummaryFormat
import com.example.healthapp.presentation.component.formatCalories
import com.example.healthapp.presentation.component.formatElapsedTime
import com.example.healthapp.presentation.component.formatHeartRate
import com.example.healthapp.R
import com.example.healthapp.presentation.exercise.ExerciseViewModel
import com.example.healthapp.presentation.theme.PsychedelicPurple
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.ItemType
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.padding
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.ListHeaderDefaults.firstItemPadding
import com.google.android.horologist.compose.material.ResponsiveListHeader
import com.google.android.horologist.compose.material.Title

@Composable
fun SummaryRoute(
    onRestartClick: () -> Unit,
    exerciseViewModel: ExerciseViewModel
) {
    val viewModel = hiltViewModel<SummaryViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SummaryScreen(uiState = uiState, onRestartClick = onRestartClick, exerciseViewModel = exerciseViewModel)
}


@OptIn(ExperimentalHorologistApi::class)
@Composable
fun SummaryScreen(
    uiState: SummaryScreenState,
    onRestartClick: () -> Unit,
    exerciseViewModel: ExerciseViewModel
) {
    val columnState = rememberResponsiveColumnState(
        contentPadding = padding(
            first = ItemType.Text,
            last = ItemType.Chip
        )
    )
    ScreenScaffold(scrollState = columnState) {
        ScalingLazyColumn(
            columnState = columnState
        ) {
            item {
                ResponsiveListHeader(contentPadding = firstItemPadding()) {
                    Title(text = stringResource(id = R.string.workout_complete))
                }
            }
            item {
                Spacer(modifier = Modifier.height(10.dp))
                SummaryFormat(
                    value = formatElapsedTime(uiState.elapsedTime, includeSeconds = true),
                    metric = stringResource(id = R.string.duration),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                Spacer(modifier = Modifier.height(10.dp))
                SummaryFormat(
                    value = formatHeartRate(uiState.averageHeartRate),
                    metric = stringResource(id = R.string.avgHR),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Spacer(modifier = Modifier.height(10.dp))
                SummaryFormat(
                    value = formatCalories(uiState.totalCalories),
                    metric = stringResource(id = R.string.calories),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Spacer(modifier = Modifier.height(10.dp))
                SummaryFormat(
                    value = formatHeartRate(uiState.maxHeartRate),
                    metric = "MaxHR",
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Spacer(modifier = Modifier.height(10.dp))
                SummaryFormat(
                    value = formatHeartRate(uiState.minHeartRate),
                    metric = "MinHR",
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Column(modifier = Modifier.fillMaxWidth(0.6f).fillMaxHeight(0.7f)) {
                    Chip(
                        label = "Home",
                        onClick = {
                            exerciseViewModel.updateIsEnded(false)
                            onRestartClick()
                        },
                        colors = ChipDefaults.chipColors(backgroundColor = PsychedelicPurple)
                    )
                }
            }
        }
    }
}

