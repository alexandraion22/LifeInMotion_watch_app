@file:OptIn(ExperimentalHorologistApi::class)

package com.example.healthapp.presentation.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.healthapp.R
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.material.Button

@Composable
fun PauseButton(onPauseClick: () -> Unit) {
    Button(
        imageVector = Icons.Default.Check,
        contentDescription = stringResource(id = R.string.pause_button_cd),
        onClick = onPauseClick
    )
}

@Preview
@Composable
fun PauseButtonPreview() {
    PauseButton { }
}
