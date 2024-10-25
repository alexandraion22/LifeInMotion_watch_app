@file:OptIn(ExperimentalHorologistApi::class)

package com.example.healthapp.presentation.component


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.healthapp.R
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.material.Button

@Composable
fun StartButton(onStartClick: () -> Unit) {
    Button(
        imageVector = Icons.Default.Call,
        contentDescription = stringResource(id = R.string.start_button_cd),
        onClick = onStartClick
    )
}

@Preview
@Composable
fun StartButtonPreview() {
    StartButton { }
}
