package ua.headway.booksummary.presentation.di

import android.annotation.SuppressLint
import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import coil.ImageLoader
import coil.memory.MemoryCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ua.headway.booksummary.presentation.ui.resources.Constants
import ua.headway.booksummary.presentation.ui.resources.Constants.UI.CROSSFADE_ENABLED

@Module
@InstallIn(SingletonComponent::class)
class MediaModule {
    @SuppressLint("UnsafeOptInUsageError") // TODO: Remove
    @Provides
    fun provideExoPlayer(@ApplicationContext context: Context): ExoPlayer =
        ExoPlayer.Builder(context) // TODO: Configure audioAttributes and repeatMode
            .setSeekForwardIncrementMs(Constants.UI.BookSummary.FAST_FORWARD_OFFSET_MILLIS)
            .setSeekBackIncrementMs(Constants.UI.BookSummary.REWIND_OFFSET_MILLIS)
            .build()

    @Provides
    fun provideImageLoader(@ApplicationContext context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .memoryCache(MemoryCache.Builder(context).build())
            .crossfade(CROSSFADE_ENABLED)
            .build()
    }
}