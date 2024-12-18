package ua.headway.booksummary.presentation.manager

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
import ua.headway.booksummary.presentation.ui.screen.booksummary.PlaybackState
import ua.headway.booksummary.presentation.util.Constants.ErrorCodes.BookSummary.ERROR_PLAYER_PLAYBACK
import ua.headway.booksummary.presentation.util.Constants.ErrorCodes.BookSummary.ERROR_PLAYER_TEMPORARILY_UNAVAILABLE
import ua.headway.booksummary.presentation.util.Constants.UI.BookSummary.AUDIO_SPEED_LEVEL_DEFAULT
import ua.headway.booksummary.presentation.util.Constants.UI.BookSummary.AUDIO_SPEED_LEVEL_MAXIMUM
import ua.headway.booksummary.presentation.util.Constants.UI.BookSummary.AUDIO_SPEED_LEVEL_MINIMUM
import ua.headway.booksummary.presentation.util.Constants.UI.BookSummary.DELAY_PLAYER_SYNC_MILLIS

class AudioPlaybackInteractorImpl : AudioPlaybackInteractor {
    private var audioPlayer: MediaController? = null
    private val audioPlayerListener = AudioPlayerListener()
    private val _playbackState: MutableStateFlow<PlaybackState> = MutableStateFlow(PlaybackState.Idle)
    private val _playbackPositionState: MutableStateFlow<Long> = MutableStateFlow(0)

    private var syncPlaybackPositionJob: Job? = null
    private var coroutineScope: CoroutineScope? = null

    private var isBuffering: Boolean = false

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
                    val isAudioPlaying = audioPlayer?.isPlaying?.takeIf { !isBuffering }
                        ?: playbackState.isAudioPlaying

                    PlaybackState.Ready(
                        isBuffering = isBuffering,
                        isAudioPlaying = isAudioPlaying,
                        currentAudioIndex = audioPlayer?.currentMediaItemIndex ?: playbackState.currentAudioIndex,
                        currentAudioPositionMs = playbackPosition,
                        currentAudioDurationMs = audioPlayer?.duration ?: playbackState.currentAudioDurationMs,
                        audioSpeedLevel = audioPlayer?.playbackParameters?.speed ?: playbackState.audioSpeedLevel
                    )
                } else playbackState
            }
        }.stateIn(coroutineScope)
    }

    override fun togglePlayback(play: Boolean) {
        audioPlayer?.run {
            if (play) {
                if (!isPlaying) {
                    play()
                }
            } else {
                if (isPlaying) {
                    pause()
                }
            }
        }
    }

    override fun seekTo(positionMs: Long) {
        val newPositionsMs = positionMs.coerceIn(0, audioPlayer?.duration)
        audioPlayer?.seekTo(newPositionsMs)
        syncPlayer()
    }

    override fun changeSpeed(speedLevel: Float) {
        speedLevel.takeIf {
            it in AUDIO_SPEED_LEVEL_MINIMUM..AUDIO_SPEED_LEVEL_MAXIMUM
        }?.let { newSpeedLevel ->
            audioPlayer?.setPlaybackSpeed(newSpeedLevel)
        }
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
                Player.STATE_READY, Player.STATE_ENDED -> {
                    isBuffering = false
                    val currentPlaybackState = _playbackState.value.asReady
                    val newPlaybackState = currentPlaybackState ?: PlaybackState.Ready(
                        isBuffering = isBuffering,
                        isAudioPlaying = audioPlayer?.isPlaying ?: false,
                        currentAudioIndex = audioPlayer?.currentMediaItemIndex ?: 0,
                        currentAudioPositionMs = audioPlayer?.currentPosition ?: 0,
                        currentAudioDurationMs = audioPlayer?.duration ?: 0,
                        audioSpeedLevel = audioPlayer?.playbackParameters?.speed ?: AUDIO_SPEED_LEVEL_DEFAULT
                    )
                    updatePlaybackState(newPlaybackState)
                    startPlayerSyncer()
                }
                else -> {
                    if (state == Player.STATE_BUFFERING) {
                        isBuffering = true
                    }
                    updatePlaybackState(newPlaybackState = PlaybackState.Idle)
                    stopPlayerSyncer()
                }
            }
            super.onPlaybackStateChanged(state)
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)

            if (mediaItem == null) return

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