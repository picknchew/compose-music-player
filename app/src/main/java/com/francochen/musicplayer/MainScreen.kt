package com.francochen.musicplayer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.francochen.musicplayer.ui.MusicPlayer
import com.francochen.musicplayer.ui.PlaybackControlType

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        MusicPlayer(
            modifier = Modifier
                .height(297.dp)
                .width(480.dp)
                .clip(RoundedCornerShape(16.dp)),
            playerState = state,
            onPlaybackControl = { type ->
                when (type) {
                    PlaybackControlType.PLAY_PAUSE -> viewModel.togglePlaying()
                    PlaybackControlType.LIKE -> viewModel.likeSong()
                    PlaybackControlType.REPEAT -> viewModel.toggleRepeat()
                    PlaybackControlType.NEXT -> viewModel.nextSong()
                    PlaybackControlType.PREVIOUS -> viewModel.previousSong()
                }
            },
            onSeek = { viewModel.seekTo(it) }
        )
    }
}