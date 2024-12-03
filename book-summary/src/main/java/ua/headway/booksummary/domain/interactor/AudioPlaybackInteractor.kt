package ua.headway.booksummary.domain.interactor

import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import ua.headway.booksummary.presentation.ui.screen.booksummary.PlaybackState

interface AudioPlaybackInteractor {
    val isPlayerAvailable: Boolean

    fun configurePlayer(
        mediaController: MediaController,
        audioItems: List<MediaItem>
    )
    suspend fun subscribeToUpdates(coroutineScope: CoroutineScope): StateFlow<PlaybackState>
    fun togglePlayback(play: Boolean)
    fun seekTo(positionMs: Long)
    fun changeSpeed(speedLevel: Float)
    fun skipForward()
    fun skipBackward()
    fun releasePlayer()
}