package ua.headway.booksummary.presentation.manager

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import ua.headway.booksummary.domain.interactor.AudioPlaybackInteractor
import ua.headway.booksummary.presentation.audio.AudioPlaybackService

interface PlayerSetupManager {
    fun setupPlayer(
        audioPlaybackInteractor: AudioPlaybackInteractor,
        context: Context,
        audioItems: List<MediaItem>,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    )
}

class BookSummaryPlayerSetupManager: PlayerSetupManager {
    override fun setupPlayer(
        audioPlaybackInteractor: AudioPlaybackInteractor,
        context: Context,
        audioItems: List<MediaItem>,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        try {
            if (!audioPlaybackInteractor.isPlayerAvailable) {
                val sessionToken = SessionToken(context, ComponentName(context, AudioPlaybackService::class.java))
                val mediaControllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
                mediaControllerFuture.addListener(
                    {
                        try {
                            mediaControllerFuture.get()?.let { mediaController ->
                                audioPlaybackInteractor.configurePlayer(mediaController, audioItems)
                                onSuccess.invoke()
                            }
                        } catch (e: Throwable) {
                            onFailure.invoke(e)
                        }
                    },
                    MoreExecutors.directExecutor()
                )
            }
        } catch (e: Throwable) {
            onFailure.invoke(e)
        }
    }
}

