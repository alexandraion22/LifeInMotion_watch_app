@file:OptIn(ExperimentalHorologistApi::class)

package com.example.healthapp.presentation.component


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import com.example.healthapp.R
import com.example.healthapp.presentation.theme.PsychedelicPurple
import com.google.android.horologist.annotations.ExperimentalHorologistApi

@Composable
fun StopButton(onEndClick: () -> Unit) {
    Button(
        onClick = onEndClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = PsychedelicPurple)
    ){
        Icon(
            painter = painterResource(id = R.drawable.ic_stop),
            contentDescription = stringResource(id = R.string.stop_button_cd),
            tint = Color.White
        )
    }
}

@Preview
@Composable
fun StopButtonPreview() {
    StopButton { }
}
