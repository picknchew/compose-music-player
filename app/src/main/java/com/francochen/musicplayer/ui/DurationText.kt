package com.francochen.musicplayer.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import kotlin.time.Duration

@Composable
fun DurationText(
    modifier: Modifier = Modifier,
    duration: Duration,
    color: Color,
    textAlign: TextAlign = TextAlign.Start,
    fontWeight: FontWeight = FontWeight.Normal,
    style: TextStyle = MaterialTheme.typography.labelMedium
) {
    Text(
        modifier = modifier,
        text = duration.toComponents { minutes, seconds, _ ->
            "%d:%02d".format(
                minutes,
                seconds
            )
        },
        textAlign = textAlign,
        fontWeight = fontWeight,
        color = color,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        style = style
    )
}