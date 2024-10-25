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
package com.example.healthapp.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.healthapp.presentation.theme.PsychedelicPurple


@Composable
fun SummaryFormat(
    value: AnnotatedString,
    metric: String,
    modifier: Modifier = Modifier
) {
    Column {
        Row(horizontalArrangement = Arrangement.Center, modifier = modifier) {
            Text(
                textAlign = TextAlign.Center,
                text = value,
                fontWeight = FontWeight.Bold,
                color = PsychedelicPurple,
                fontSize = 25.sp
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Row(horizontalArrangement = Arrangement.Center, modifier = modifier) {
            Text(
                textAlign = TextAlign.Center, text = metric, fontSize = 12.sp
            )
        }
    }

}

@Preview
@Composable
fun SummaryFormatPreview() {
    SummaryFormat(value = buildAnnotatedString { append("5.3") }, metric = "km")
}
