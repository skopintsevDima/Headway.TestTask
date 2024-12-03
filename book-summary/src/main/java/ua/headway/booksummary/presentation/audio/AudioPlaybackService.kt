package ua.headway.booksummary.presentation.audio

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CommandButton
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import coil.ImageLoader
import coil.request.ImageRequest
import com.google.common.collect.ImmutableList
import dagger.hilt.android.AndroidEntryPoint
import ua.headway.booksummary.presentation.ui.resources.LocalResources
import javax.inject.Inject

@SuppressLint("UnsafeOptInUsageError")
@AndroidEntryPoint
class AudioPlaybackService: MediaSessionService() {
    @Inject
    lateinit var player: ExoPlayer
    private var mediaSession: MediaSession? = null
    @Inject
    lateinit var imageLoader: ImageLoader
    private lateinit var playerNotificationManager: PlayerNotificationManager

    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSession.Builder(this, player)
            .build()
            .also(::addSession)
        configureMediaPlaybackNotification()
    }

    private fun configureMediaPlaybackNotification() {
        setMediaNotificationProvider(
            DefaultMediaNotificationProvider(
                this,
                { NOTIFICATION_ID },
                NOTIFICATION_CHANNEL_ID,
                LocalResources.Strings.NotificationChannelName
            )
        )
//        setMediaNotificationProvider(object : MediaNotification.Provider {
//            override fun createNotification(
//                mediaSession: MediaSession,
//                mediaButtonPreferences: ImmutableList<CommandButton>,
//                actionFactory: MediaNotification.ActionFactory,
//                onNotificationChangedCallback: MediaNotification.Provider.Callback
//            ): MediaNotification {
//                return MediaNotification(
//                    NOTIFICATION_ID,
//                    NotificationCompat.Builder(
//                        this@AudioPlaybackService,
//                        NOTIFICATION_CHANNEL_ID
//                    )
//
//                        .build()
//                )
//            }
//
//            override fun handleCustomCommand(
//                session: MediaSession,
//                action: String,
//                extras: Bundle
//            ): Boolean {
//                TODO("Not yet implemented")
//            }
//
//        })
//        playerNotificationManager = PlayerNotificationManager.Builder(
//            this,
//            NOTIFICATION_ID,
//            NOTIFICATION_CHANNEL_ID
//        )
//            .setMediaDescriptionAdapter(DescriptionAdapter())
//            .setRewindActionIconResourceId(LocalResources.Icons.Rewind5)
//            .setFastForwardActionIconResourceId(LocalResources.Icons.Forward10)
//            .setNextActionIconResourceId(LocalResources.Icons.SkipForward)
//            .setPreviousActionIconResourceId(LocalResources.Icons.SkipBack)
//            .setPlayActionIconResourceId(LocalResources.Icons.Play)
//            .setPauseActionIconResourceId(LocalResources.Icons.Pause)
//            .build()
//            .apply {
//                setPlayer(player)
//                mediaSession?.platformToken?.let(::setMediaSessionToken)
//                setUseRewindAction(true)
//                setUseFastForwardAction(true)
//                setUseNextAction(true)
//                setUsePreviousAction(true)
//                setColorized(true)
//                setColor(LocalResources.Colors.Blue.toArgb())
//            }
    }

    override fun onGetSession(
        controllerInfo: MediaSession.ControllerInfo
    ): MediaSession? = mediaSession

    override fun onDestroy() {
        playerNotificationManager.setPlayer(null)
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    private inner class DescriptionAdapter : PlayerNotificationManager.MediaDescriptionAdapter {
        override fun createCurrentContentIntent(player: Player): PendingIntent? {
            return PendingIntent.getActivity(
                this@AudioPlaybackService,
                RC_OPEN_PLAYER_ACTIVITY,
                Intent().apply {
                    setClassName(this@AudioPlaybackService, "ua.headway.headwaytesttask.presentation.ui.MainActivity")
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        override fun getCurrentContentTitle(player: Player): String {
            return player.currentMediaItem?.mediaMetadata?.title.toString()
        }

        override fun getCurrentContentText(player: Player): String? {
            return player.currentMediaItem?.mediaMetadata?.description?.toString()
        }

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            val artworkUri = player.currentMediaItem?.mediaMetadata?.artworkUri
            if (artworkUri != null) {
                val request = ImageRequest.Builder(this@AudioPlaybackService)
                    .data(artworkUri)
                    .allowHardware(false)
                    .target(
                        onSuccess = { drawable ->
                            val bitmap = (drawable as BitmapDrawable).bitmap
                            callback.onBitmap(bitmap)
                        },
                        onError = {
                            // TODO: Handle error and use error placeholder
                        }
                    )
                    .build()

                imageLoader.enqueue(request)
            }
            return null
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 1984
        const val NOTIFICATION_CHANNEL_ID = "ua.headway.headwaytesttask.audio_playback"

        private const val RC_OPEN_PLAYER_ACTIVITY = 200
    }
}