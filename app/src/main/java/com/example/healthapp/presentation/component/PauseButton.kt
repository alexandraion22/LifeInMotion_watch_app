@file:OptIn(ExperimentalHorologistApi::class)

package com.example.healthapp.presentation.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import com.example.healthapp.R
import com.example.healthapp.presentation.theme.PsychedelicPurple
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.material.Button

@Composable
fun PauseButton(onPauseClick: () -> Unit) {
    androidx.wear.compose.material.Button(
        onClick = onPauseClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = PsychedelicPurple)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_pause),
            contentDescription = stringResource(id = R.string.resume_button_cd),
            tint = Color.White
        )
    }
}

@Preview
@Composable
fun PauseButtonPreview() {
    PauseButton { }
}
