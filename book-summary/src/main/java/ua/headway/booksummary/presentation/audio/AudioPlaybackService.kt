package ua.headway.booksummary.presentation.audio

import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AudioPlaybackService: MediaSessionService() {
    @Inject
    lateinit var player: ExoPlayer
    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSession.Builder(this, player).build()
            .also(::addSession)
        configureMediaPlaybackNotification()
    }

    private fun configureMediaPlaybackNotification() {
        // TODO: Setup notification: https://developer.android.com/media/implement/playback-app?source=post_page-----16ae8c35f955--------------------------------#publishing_a_notification
    }

    override fun onGetSession(
        controllerInfo: MediaSession.ControllerInfo
    ): MediaSession? = mediaSession

    override fun onDestroy() {
        // TODO: Remove notification ?
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}