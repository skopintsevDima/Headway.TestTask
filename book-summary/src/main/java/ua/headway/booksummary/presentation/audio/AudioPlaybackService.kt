package ua.headway.booksummary.presentation.audio

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import dagger.hilt.android.AndroidEntryPoint
import ua.headway.booksummary.presentation.ui.resources.LocalResources
import javax.inject.Inject

@SuppressLint("UnsafeOptInUsageError")
@AndroidEntryPoint
class AudioPlaybackService: MediaSessionService() {
    @Inject
    lateinit var player: ExoPlayer
    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(getOpenPlayerActivityIntent())
            .build()
            .also(::addSession)
        configureMediaPlaybackNotification()
    }

    private fun getOpenPlayerActivityIntent(): PendingIntent = PendingIntent.getActivity(
        this@AudioPlaybackService,
        RC_OPEN_PLAYER_ACTIVITY,
        Intent().apply {
            setClassName(this@AudioPlaybackService, PLAYER_ACTIVITY_CLASS_NAME)
            addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    private fun configureMediaPlaybackNotification() {
        setMediaNotificationProvider(
            DefaultMediaNotificationProvider(
                this,
                { NOTIFICATION_ID },
                NOTIFICATION_CHANNEL_ID,
                LocalResources.Strings.NotificationChannelName
            )
        )
    }

    override fun onGetSession(
        controllerInfo: MediaSession.ControllerInfo
    ): MediaSession? = mediaSession

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    companion object {
        private const val NOTIFICATION_ID = 1984
        const val NOTIFICATION_CHANNEL_ID = "ua.headway.headwaytesttask.audio_playback"

        private const val RC_OPEN_PLAYER_ACTIVITY = 200
        private const val PLAYER_ACTIVITY_CLASS_NAME = "ua.headway.headwaytesttask.presentation.ui.MainActivity"
    }
}