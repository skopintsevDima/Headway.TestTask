package ua.headway.booksummary.presentation.di

import android.annotation.SuppressLint
import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ua.headway.booksummary.presentation.ui.resources.Constants
import ua.headway.booksummary.presentation.ui.resources.Constants.UI.BookSummary.PLAYER_HANDLE_FOCUS
import ua.headway.booksummary.presentation.ui.resources.Constants.UI.BookSummary.PLAYER_PLAY_WHEN_READY
import ua.headway.booksummary.presentation.ui.resources.Constants.UI.BookSummary.PLAYER_REPEAT_MODE

@Module
@InstallIn(SingletonComponent::class)
class MediaModule {
    @Provides
    fun provideAudioAttributes(): AudioAttributes = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_SPEECH)
        .setUsage(C.USAGE_MEDIA)
        .build()

    @SuppressLint("UnsafeOptInUsageError")
    @Provides
    fun provideExoPlayer(
        @ApplicationContext context: Context,
        audioAttributes: AudioAttributes
    ): ExoPlayer = ExoPlayer.Builder(context)
        .setSeekForwardIncrementMs(Constants.UI.BookSummary.FAST_FORWARD_OFFSET_MILLIS)
        .setSeekBackIncrementMs(Constants.UI.BookSummary.REWIND_OFFSET_MILLIS)
        .setAudioAttributes(audioAttributes, PLAYER_HANDLE_FOCUS)
        .build().apply {
            playWhenReady = PLAYER_PLAY_WHEN_READY
            repeatMode = PLAYER_REPEAT_MODE
        }
}