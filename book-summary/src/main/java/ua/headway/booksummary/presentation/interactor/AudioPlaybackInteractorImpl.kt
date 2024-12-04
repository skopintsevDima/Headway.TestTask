package ua.headway.booksummary.presentation.interactor

import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.headway.booksummary.domain.interactor.AudioPlaybackInteractor
import ua.headway.booksummary.presentation.ui.resources.Constants.ErrorCodes.BookSummary.ERROR_PLAYER_PLAYBACK
import ua.headway.booksummary.presentation.ui.resources.Constants.UI.BookSummary.DELAY_PLAYER_POSITION_UPDATES_MILLIS
import ua.headway.booksummary.presentation.ui.screen.booksummary.PlaybackState

class AudioPlaybackInteractorImpl : AudioPlaybackInteractor {
    private var audioPlayer: MediaController? = null
    private val audioPlayerListener = AudioPlayerListener()
    private val _playbackState: MutableStateFlow<PlaybackState> = MutableStateFlow(PlaybackState.Idle)
    private val _playbackPositionState: MutableStateFlow<Long> = MutableStateFlow(0)

    private var syncPlaybackPositionJob: Job? = null
    private var coroutineScope: CoroutineScope? = null

    override val isPlayerAvailable: Boolean
        get() = audioPlayer != null

    override fun configurePlayer(
        mediaController: MediaController,
        audioItems: List<MediaItem>
    ) {
        audioPlayer = mediaController.apply {
            addListener(audioPlayerListener)
            setMediaItems(audioItems)
            prepare()
            startPositionSyncer()
        }
    }

    override suspend fun subscribeToUpdates(coroutineScope: CoroutineScope): StateFlow<PlaybackState> {
        this.coroutineScope = coroutineScope
        return _playbackState.combine(_playbackPositionState) { playbackState, playbackPosition ->
            if (playbackState is PlaybackState.Ready){
                playbackState.copy(currentAudioPositionMs = playbackPosition)
            } else playbackState
        }.stateIn(coroutineScope)
    }

    override fun togglePlayback(play: Boolean) {
        if (play && audioPlayer?.isPlaying == false) {
            audioPlayer?.play()
        } else {
            audioPlayer?.pause()
        }
    }

    override fun seekTo(positionMs: Long) {
        audioPlayer?.seekTo(positionMs)
        syncPlayerPosition()
    }

    override fun changeSpeed(speedLevel: Float) {
        audioPlayer?.setPlaybackSpeed(speedLevel)
    }

    override fun skipForward() {
        audioPlayer?.seekToNext()
        syncPlayerPosition()
    }

    override fun skipBackward() {
        audioPlayer?.seekToPrevious()
        syncPlayerPosition()
    }

    override fun releasePlayer() {
        stopPositionSyncer()
        audioPlayer?.apply {
            removeListener(audioPlayerListener)
            stop()
            release()
        }
        audioPlayer = null
    }

    private fun startPositionSyncer() {
        if (syncPlaybackPositionJob == null) {
            syncPlaybackPositionJob = coroutineScope?.launch {
                while (isActive) {
                    delay(DELAY_PLAYER_POSITION_UPDATES_MILLIS)
                    withContext(Dispatchers.Main) {
                        syncPlayerPosition()
                    }
                }
            }
        }
    }

    private fun syncPlayerPosition() {
        val newAudioPositionMs = audioPlayer?.currentPosition ?: 0
        coroutineScope?.launch { _playbackPositionState.emit(newAudioPositionMs) }
    }

    private fun stopPositionSyncer() {
        syncPlaybackPositionJob?.cancel()
        syncPlaybackPositionJob = null
    }

    private fun updatePlaybackState(newPlaybackState: PlaybackState) {
        coroutineScope?.launch { _playbackState.emit(newPlaybackState) }
    }

    private inner class AudioPlayerListener : Player.Listener {
        override fun onPlaybackStateChanged(state: Int) {
            when (state) {
                Player.STATE_READY -> {
                    val currentPlaybackState = _playbackState.value.asReady
                    val newPlaybackState = currentPlaybackState ?: PlaybackState.Ready(
                        isAudioPlaying = audioPlayer?.isPlaying ?: false,
                        currentAudioIndex = audioPlayer?.currentMediaItemIndex ?: 0,
                        currentAudioPositionMs = audioPlayer?.currentPosition ?: 0,
                        currentAudioDurationMs = audioPlayer?.duration ?: 0
                    )
                    updatePlaybackState(newPlaybackState)
                    startPositionSyncer()
                }
                else -> {
                    updatePlaybackState(newPlaybackState = PlaybackState.Idle)
                    stopPositionSyncer()
                }
            }
            super.onPlaybackStateChanged(state)
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            val currentPlaybackState = _playbackState.value.asReady
            if (currentPlaybackState != null) {
                updatePlaybackState(newPlaybackState = currentPlaybackState.copy(isAudioPlaying = isPlaying))
            }
            super.onIsPlayingChanged(isPlaying)
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            val currentPlaybackState = _playbackState.value.asReady
            val newAudioDurationMs = audioPlayer?.duration ?: 0 // TODO: error if null
            val newAudioIndex = audioPlayer?.currentMediaItemIndex ?: 0 // TODO: error if null
            if (currentPlaybackState != null) {
                val newPlaybackState = currentPlaybackState.copy(
                    currentAudioIndex = newAudioIndex,
                    currentAudioPositionMs = 0,
                    currentAudioDurationMs = newAudioDurationMs
                )
                updatePlaybackState(newPlaybackState)
            }
            super.onMediaItemTransition(mediaItem, reason)
        }

        override fun onPlayerError(error: PlaybackException) {
            updatePlaybackState(newPlaybackState = PlaybackState.Error(
                ERROR_PLAYER_PLAYBACK,
                error.message.toString()
            ))
            stopPositionSyncer()
            super.onPlayerError(error)
        }
    }
}