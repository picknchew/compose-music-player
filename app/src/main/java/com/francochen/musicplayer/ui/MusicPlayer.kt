@file:OptIn(ExperimentalMaterial3Api::class)

package com.francochen.musicplayer.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import coil3.compose.AsyncImage
import com.francochen.musicplayer.MusicPlayerState
import com.francochen.musicplayer.R
import com.francochen.musicplayer.ui.theme.MusicPlayerTheme
import kotlin.time.Duration

private object Dimensions {
    const val PADDING = 32
    const val THUMB_SIZE = 12
    const val TRACK_HEIGHT = 3
    const val ALBUM_COVER_SIZE = 88
    const val CONTROLS_SPACING = 24
}

private val LIKE_COLOR = Color(0xFFE57373)

@Composable
fun MusicPlayer(
    modifier: Modifier = Modifier,
    playerState: MusicPlayerState = MusicPlayerState(),
    onPlaybackControl: (PlaybackControlType) -> Unit = {},
    onSeek: (Int) -> Unit = {}
) {
    val childPaddingModifier = Modifier.padding(horizontal = Dimensions.THUMB_SIZE.dp / 2)
    Column(
        modifier
            .background(color = MaterialTheme.colorScheme.surfaceContainer)
            // Align everything else to SeekBar track https://issuetracker.google.com/issues/291121604
            .padding(
                horizontal = Dimensions.PADDING.dp - Dimensions.THUMB_SIZE.dp / 2,
                vertical = Dimensions.PADDING.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TrackInfo(modifier = childPaddingModifier, playerState.title, playerState.artists)
        Spacer(Modifier.weight(1f))
        SeekBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp),
            bufferedTime = playerState.bufferedTime,
            elapsedTime = playerState.elapsedTime,
            totalTime = playerState.totalTime,
            onSeek = onSeek
        )
        Spacer(Modifier.weight(0.5f))
        PlaybackControls(
            isLiked = playerState.isLiked,
            isPlaying = playerState.isPlaying,
            isRepeat = playerState.isRepeat,
            onPlaybackControl = onPlaybackControl,
        )
    }
}

@Composable
fun TrackInfo(modifier: Modifier, title: String, artists: List<String>) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Album cover
        AsyncImage(
            model = R.drawable.album_cover,
            contentDescription = "Album Cover",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(Dimensions.ALBUM_COVER_SIZE.dp)
        )

        Column {
            Text(
                text = title,
//                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = artists.joinToString(", "),
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                maxLines = 1,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun SeekBar(
    modifier: Modifier = Modifier,
    bufferedTime: Duration,
    elapsedTime: Duration,
    totalTime: Duration,
    onSeek: (newValue: Int) -> Unit = {}
) {
    val sliderColors = SliderDefaults.colors(
        thumbColor = MaterialTheme.colorScheme.onPrimary,
        disabledThumbColor = MaterialTheme.colorScheme.onPrimary,
        activeTrackColor = MaterialTheme.colorScheme.onPrimary,
        inactiveTrackColor = Color.White.copy(alpha = 0.2f)
    )
    val interactionSource = remember { MutableInteractionSource() }

    val bufferedPct = (bufferedTime / totalTime).toFloat()

    Column(modifier) {
        Slider(
            modifier = Modifier
                .fillMaxWidth()
                .height(max(Dimensions.TRACK_HEIGHT.dp, Dimensions.THUMB_SIZE.dp)),
            valueRange = 0f..totalTime.inWholeSeconds.toFloat(),
            value = elapsedTime.inWholeSeconds.toFloat(),
            onValueChange = { onSeek(it.toInt()) },
            colors = sliderColors,
            thumb = {
                SliderDefaults.Thumb(
                    colors = sliderColors,
                    interactionSource = interactionSource,
                    // Add offset as a workaround to center thumb with custom size due to
                    // https://issuetracker.google.com/issues/254417424.
                    modifier = Modifier.offset(
                        y = max(
                            (16.dp - Dimensions.THUMB_SIZE.dp) / 2,
                            0.dp
                        )
                    ),
                    thumbSize = DpSize(Dimensions.THUMB_SIZE.dp, Dimensions.THUMB_SIZE.dp)
                )
            },
            track = { sliderState ->
                SliderDefaults.Track(
                    colors = sliderColors,
                    modifier = Modifier
                        .height(Dimensions.TRACK_HEIGHT.dp)
                        .drawWithCache {
                            onDrawBehind {
                                drawRect(
                                    color = Color.White.copy(alpha = 0.5f),
                                    size = Size(this.size.width * bufferedPct, this.size.height)
                                )
                            }
                        },
                    sliderState = sliderState,
                    thumbTrackGapSize = 0.dp,
                    drawStopIndicator = {}
                )
            },
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimensions.THUMB_SIZE.dp / 2, vertical = 3.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DurationText(
                duration = elapsedTime,
                textAlign = TextAlign.Left,
                color = Color.White.copy(alpha = 0.7f)
            )
            DurationText(
                duration = totalTime,
                textAlign = TextAlign.Right,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun PlaybackControlButton(
    modifier: Modifier = Modifier,
    iconSize: Dp = 24.dp,
    iconColor: Color = MaterialTheme.colorScheme.inverseOnSurface,
    @DrawableRes iconId: Int,
    contentDescription: String,
    onClick: () -> Unit
) {
    IconButton(modifier = modifier.size(36.dp), onClick = onClick) {
        Icon(
            modifier = Modifier.size(iconSize),
            painter = painterResource(iconId),
            contentDescription = contentDescription,
            tint = iconColor
        )
    }
}

enum class PlaybackControlType {
    REPEAT, PREVIOUS, NEXT, PLAY_PAUSE, LIKE
}

@Composable
fun PlaybackControls(
    modifier: Modifier = Modifier,
    isLiked: Boolean,
    isPlaying: Boolean,
    isRepeat: Boolean,
    onPlaybackControl: (PlaybackControlType) -> Unit = {}
) {
    Row(
        modifier,
        horizontalArrangement = Arrangement.spacedBy(Dimensions.CONTROLS_SPACING.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlaybackControlButton(
            onClick = { onPlaybackControl(PlaybackControlType.REPEAT) },
            iconId = R.drawable.ic_repeat,
            iconColor = if (isRepeat) MaterialTheme.colorScheme.surfaceTint else MaterialTheme.colorScheme.inverseOnSurface,
            contentDescription = "Repeat"
        )
        PlaybackControlButton(
            iconSize = 36.dp,
            onClick = { onPlaybackControl(PlaybackControlType.PREVIOUS) },
            iconId = R.drawable.ic_skip_previous,
            contentDescription = "Previous Song"
        )
        PlaybackControlButton(
            modifier = Modifier
                .size(72.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
            iconSize = 48.dp,
            onClick = {
                onPlaybackControl(PlaybackControlType.PLAY_PAUSE)
            },
            iconId = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play_arrow,
            contentDescription = if (isPlaying) "Pause Song" else "Play Song"
        )
        PlaybackControlButton(
            iconSize = 36.dp,
            onClick = { onPlaybackControl(PlaybackControlType.NEXT) },
            iconId = R.drawable.ic_skip_next,
            contentDescription = "Next Song"
        )
        PlaybackControlButton(
            onClick = { onPlaybackControl(PlaybackControlType.LIKE) },
            iconId = if (isLiked) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_outline,
            iconColor = if (isLiked) LIKE_COLOR else MaterialTheme.colorScheme.inverseOnSurface,
            contentDescription = "Like Song"
        )
    }
}

@Preview(
    showBackground = true,
    heightDp = 297,
    widthDp = 480
)
@Composable
fun MusicPlayerPreview() {
    MusicPlayerTheme {
        MusicPlayer()
    }
}
