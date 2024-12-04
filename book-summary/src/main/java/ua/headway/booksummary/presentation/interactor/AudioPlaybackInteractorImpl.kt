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
import ua.headway.booksummary.presentation.ui.resources.Constants.ErrorCodes.BookSummary.ERROR_PLAYER_TEMPORARILY_UNAVAILABLE
import ua.headway.booksummary.presentation.ui.resources.Constants.UI.BookSummary.DELAY_PLAYER_SYNC_MILLIS
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
            startPlayerSyncer()
        }
    }

    override suspend fun subscribeToUpdates(coroutineScope: CoroutineScope): StateFlow<PlaybackState> {
        this.coroutineScope = coroutineScope
        return _playbackState.combine(_playbackPositionState) { playbackState, playbackPosition ->
            withContext(Dispatchers.Main) {
                if (playbackState is PlaybackState.Ready){
                    PlaybackState.Ready(
                        isAudioPlaying = audioPlayer?.isPlaying ?: playbackState.isAudioPlaying,
                        currentAudioIndex = audioPlayer?.currentMediaItemIndex ?: playbackState.currentAudioIndex,
                        currentAudioPositionMs = playbackPosition,
                        currentAudioDurationMs = audioPlayer?.duration ?: playbackState.currentAudioDurationMs,
                    )
                } else playbackState
            }
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
        syncPlayer()
    }

    override fun changeSpeed(speedLevel: Float) {
        audioPlayer?.setPlaybackSpeed(speedLevel)
    }

    override fun skipForward() {
        audioPlayer?.seekToNext()
        syncPlayer()
    }

    override fun skipBackward() {
        audioPlayer?.seekToPrevious()
        syncPlayer()
    }

    override fun releasePlayer() {
        stopPlayerSyncer()
        audioPlayer?.apply {
            removeListener(audioPlayerListener)
            stop()
            release()
        }
        audioPlayer = null
    }

    private fun startPlayerSyncer() {
        if (syncPlaybackPositionJob == null) {
            syncPlaybackPositionJob = coroutineScope?.launch {
                while (isActive) {
                    delay(DELAY_PLAYER_SYNC_MILLIS)
                    withContext(Dispatchers.Main) {
                        syncPlayer()
                    }
                }
            }
        }
    }

    private fun syncPlayer() {
        audioPlayer?.run {
            val newAudioPositionMs = this.currentPosition
            coroutineScope?.launch { _playbackPositionState.emit(newAudioPositionMs) }
        }
    }

    private fun stopPlayerSyncer() {
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
                    startPlayerSyncer()
                }
                else -> {
                    updatePlaybackState(newPlaybackState = PlaybackState.Idle)
                    stopPlayerSyncer()
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
            super.onMediaItemTransition(mediaItem, reason)
            audioPlayer?.run {
                val currentPlaybackState = _playbackState.value.asReady
                val newAudioDurationMs = this.duration
                val newAudioIndex = this.currentMediaItemIndex
                if (currentPlaybackState != null) {
                    val newPlaybackState = currentPlaybackState.copy(
                        currentAudioIndex = newAudioIndex,
                        currentAudioPositionMs = 0,
                        currentAudioDurationMs = newAudioDurationMs
                    )
                    updatePlaybackState(newPlaybackState)
                }
            } ?: updatePlaybackState(newPlaybackState = PlaybackState.Error(
                ERROR_PLAYER_TEMPORARILY_UNAVAILABLE
            ))
        }

        override fun onPlayerError(error: PlaybackException) {
            updatePlaybackState(newPlaybackState = PlaybackState.Error(
                ERROR_PLAYER_PLAYBACK,
                error.message.toString()
            ))
            stopPlayerSyncer()
            super.onPlayerError(error)
        }
    }
}