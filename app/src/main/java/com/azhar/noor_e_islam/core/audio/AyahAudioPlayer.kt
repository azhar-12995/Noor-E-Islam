package com.azhar.noor_e_islam.core.audio

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * App-wide ExoPlayer wrapper used to stream ayah audio (and any other media)
 * with a lightweight observable state.
 *
 * State:
 *  - [currentUrl] — the URL currently loaded (or null)
 *  - [isPlaying]  — true while playback is active
 *  - [completions] — emits a [Unit] every time a track finishes naturally
 *                    (used by "Play All" to advance to the next ayah).
 */
@Singleton
class AyahAudioPlayer @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private var player: ExoPlayer? = null

    private val _currentUrl = MutableStateFlow<String?>(null)
    val currentUrl: StateFlow<String?> = _currentUrl

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _completions = MutableSharedFlow<Unit>(
        extraBufferCapacity = 4,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val completions: SharedFlow<Unit> = _completions

    private fun player(): ExoPlayer = player ?: ExoPlayer.Builder(context).build().also {
        it.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                _isPlaying.value = playing
            }

            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    _isPlaying.value = false
                    _currentUrl.value = null
                    _completions.tryEmit(Unit)
                }
            }
        })
        player = it
    }

    /** Play the given URL (toggles pause if the same URL is already loaded). */
    fun toggle(url: String) {
        val p = player()
        if (_currentUrl.value == url && p.isPlaying) {
            p.pause()
            return
        }
        if (_currentUrl.value != url) {
            p.setMediaItem(MediaItem.fromUri(url))
            p.prepare()
            _currentUrl.value = url
        }
        p.playWhenReady = true
        p.play()
    }

    /** Force-play [url] from the start (used by Play-All sequencing). */
    fun play(url: String) {
        val p = player()
        p.setMediaItem(MediaItem.fromUri(url))
        p.prepare()
        _currentUrl.value = url
        p.playWhenReady = true
        p.play()
    }

    fun pause() {
        player?.pause()
    }

    fun stop() {
        player?.stop()
        _isPlaying.value = false
        _currentUrl.value = null
    }

    fun release() {
        player?.release()
        player = null
        _isPlaying.value = false
        _currentUrl.value = null
    }
}

