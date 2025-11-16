package com.francochen.musicplayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

data class MusicPlayerState(
    val title: String = "Black Friday (pretty like the sun)",
    val artists: List<String> = listOf("Lost Frequencies", "Tom Odell", "Poppy Baskcomb"),
    val isLiked: Boolean = false,
    val isPlaying: Boolean = false,
    val isRepeat: Boolean = false,
    val elapsedTime: Duration = 0.seconds,
    val totalTime: Duration = 5.minutes + 11.seconds,
    val bufferedTime: Duration = 23.seconds
)

class MainViewModel : ViewModel() {
    private var playJob: Job? = null

    private val _state = MutableStateFlow(MusicPlayerState())
    val state: StateFlow<MusicPlayerState> = _state

    init {
        viewModelScope.launch {
            // Mimic buffering.
            while (true) {
                delay(0.5.seconds)
                var newBufferedTime = _state.value.bufferedTime + (1..20).random().seconds
                if (newBufferedTime > _state.value.totalTime) {
                    newBufferedTime = _state.value.totalTime
                }

                _state.value = _state.value.copy(bufferedTime = newBufferedTime)
            }
        }
    }

    fun startPlaying() {
        // Mimic elapsed time and buffering.
        playJob = viewModelScope.launch {
            while (true) {
                delay(1.seconds)
                // Reached end of song, start from beginning.
                if (_state.value.elapsedTime == _state.value.totalTime) {
                    _state.value =
                        _state.value.copy(elapsedTime = 0.seconds, bufferedTime = 0.seconds)
                } else {
                    _state.value =
                        _state.value.copy(elapsedTime = _state.value.elapsedTime + 1.seconds)
                }
            }
        }
    }

    fun likeSong() {
        _state.value = _state.value.copy(isLiked = !_state.value.isLiked)
    }

    fun togglePlaying() {
        _state.value = _state.value.copy(isPlaying = !_state.value.isPlaying)

        if (_state.value.isPlaying) {
            startPlaying()
        } else {
            playJob?.cancel()
        }
    }

    fun toggleRepeat() {
        _state.value = _state.value.copy(isRepeat = !_state.value.isRepeat)
    }

    fun seekTo(newValue: Int) {
        _state.value = _state.value.copy(elapsedTime = newValue.seconds)
    }

    fun nextSong() {
        _state.value = _state.value.copy(elapsedTime = 0.seconds, bufferedTime = 0.seconds)
    }

    fun previousSong() {
        _state.value = _state.value.copy(elapsedTime = 0.seconds, bufferedTime = 0.seconds)
    }
}