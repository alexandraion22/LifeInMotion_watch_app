package com.example.healthapp.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Shapes
import androidx.wear.compose.material.Typography

@Composable
fun ExerciseSampleTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = wearColorPalette,
        typography = Typography,
        content = content,
        shapes = Shapes
    )
}

internal val wearColorPalette: Colors = Colors(
    primary = Color(48, 49, 51),
    primaryVariant = Color.LightGray,
    error = Color.Red,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onError = Color.Black
)

internal val Typography = Typography(
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    )
)

val Shapes = Shapes(
    small = RoundedCornerShape(100.dp),
    medium = RoundedCornerShape(100.dp),
    large = RoundedCornerShape(100.dp)
)

val LightPurple = Color(0xFFAE79F2)
val PsychedelicPurple = Color(0xFF761CEA)
val DarkPurple = Color(0xFF58229D)
val CoolGray = Color(0xFF333333)
val VeryLightGray = Color(0XFFF0F0F0)
val KindaLightGray = Color(0xFFE2E8F0)