package com.example.healthapp.presentation.home

import android.content.ContentValues
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.withSaveLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.example.healthapp.R
import com.example.healthapp.data.ServiceState
import com.example.healthapp.presentation.theme.PsychedelicPurple
import com.example.healthapp.presentation.theme.VeryLightGray
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.ambient.AmbientState
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun HomeRoute(
    ambientState: AmbientState,
    onNoExerciseCapabilities: () -> Unit,
    onStartSensors: () -> Unit,
    onStopSensors: () -> Unit,
    onPrepareExercise: (String) -> Unit
) {
    val viewModel = hiltViewModel<HomeViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    /** Request permissions prior to launching exercise.**/
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result.all { it.value }) {
            Log.d(ContentValues.TAG, "All required permissions granted")
        }
    }

    SideEffect {
        val preparingState = uiState
        if (preparingState is HomeScreenState.Home && !preparingState.hasExerciseCapabilities) {
            onNoExerciseCapabilities()
        }
    }

    if (uiState.serviceState is ServiceState.Connected) {
        val requiredPermissions = uiState.requiredPermissions
        LaunchedEffect(requiredPermissions) {
            permissionLauncher.launch(requiredPermissions.toTypedArray())
        }
    }

    val columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ScalingLazyColumnDefaults.ItemType.Text,
            last = ScalingLazyColumnDefaults.ItemType.Chip
        )
    )

    ScreenScaffold(scrollState = columnState) {
        ScalingLazyColumn(
            columnState = columnState
        ) {
            item {
                Column{
                    Row {
                        Button(
                            onClick = { onPrepareExercise("circuit_training") },
                            colors = ButtonDefaults.buttonColors(backgroundColor = VeryLightGray),
                            modifier = Modifier
                                .size(85.dp)
                                .padding(4.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_circuit_training),
                                    contentDescription = "Circuit Training",
                                    tint = PsychedelicPurple,
                                    modifier = Modifier.size(40.dp)
                                )
                                Text("Circuit", color = PsychedelicPurple, fontSize = 8.sp)
                                Text("Training", color = PsychedelicPurple, fontSize = 8.sp)
                            }
                        }
                        Button(
                            onClick = { onPrepareExercise("aerobic")},
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                            modifier = Modifier
                                .size(85.dp)
                                .padding(4.dp)
                        )
                        {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_aerobic),
                                    contentDescription = "Aerobic",
                                    tint = PsychedelicPurple,
                                    modifier = Modifier.size(40.dp)
                                )
                                Text("Aerobic", color = PsychedelicPurple, fontSize = 8.sp)
                            }
                        }
                    }
                    Row {
                        Button(
                            onClick = { onPrepareExercise("weights") },
                            colors = ButtonDefaults.buttonColors(backgroundColor = VeryLightGray),
                            modifier = Modifier
                                .size(85.dp)
                                .padding(4.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_weightlifting),
                                    contentDescription = "Weightlifting",
                                    tint = PsychedelicPurple,
                                    modifier = Modifier.size(40.dp)
                                )
                                Text("Weightlifting", color = PsychedelicPurple, fontSize = 8.sp)
                            }
                        }
                        Button(
                            onClick = {onPrepareExercise("pilates")},
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                            modifier = Modifier
                                .size(85.dp)
                                .padding(4.dp)
                        )
                        {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_pilates),
                                    contentDescription = "Pilates",
                                    tint = PsychedelicPurple,
                                    modifier = Modifier.size(40.dp)
                                )
                                Text("Pilates", color = PsychedelicPurple, fontSize = 8.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            item {
                BackgroundServicesContent(onStartSensors, onStopSensors)
            }
        }
    }
}

private val grayscale = Paint().apply {
    colorFilter = ColorFilter.colorMatrix(
        ColorMatrix().apply {
            setToSaturation(0f)
        }
    )
    isAntiAlias = false
}

internal fun Modifier.ambientGray(ambientState: AmbientState): Modifier =
    if (ambientState is AmbientState.Ambient) {
        graphicsLayer {
            scaleX = 0.9f
            scaleY = 0.9f
        }.drawWithContent {
            drawIntoCanvas {
                it.withSaveLayer(size.toRect(), grayscale) {
                    drawContent()
                }
            }
        }
    } else {
        this
    }
