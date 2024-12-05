package ua.headway.booksummary.presentation.di

import android.annotation.SuppressLint
import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.database.ExoDatabaseProvider
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSink
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ua.headway.booksummary.presentation.ui.resources.Constants
import ua.headway.booksummary.presentation.ui.resources.Constants.UI.BookSummary.AUDIO_CACHE_DIR_NAME
import ua.headway.booksummary.presentation.ui.resources.Constants.UI.BookSummary.PLAYER_CACHE_SIZE_MB
import ua.headway.booksummary.presentation.ui.resources.Constants.UI.BookSummary.PLAYER_HANDLE_FOCUS
import ua.headway.booksummary.presentation.ui.resources.Constants.UI.BookSummary.PLAYER_PLAY_WHEN_READY
import ua.headway.booksummary.presentation.ui.resources.Constants.UI.BookSummary.PLAYER_REPEAT_MODE
import java.io.File

@SuppressLint("UnsafeOptInUsageError")
@Module
@InstallIn(SingletonComponent::class)
class MediaModule {
    @Provides
    fun provideAudioCache(
        @ApplicationContext context: Context
    ): SimpleCache {
        val cacheDir = File(context.cacheDir, AUDIO_CACHE_DIR_NAME)
        val cacheEvictor = LeastRecentlyUsedCacheEvictor(PLAYER_CACHE_SIZE_MB)
        val databaseProvider = ExoDatabaseProvider(context)

        return SimpleCache(cacheDir, cacheEvictor, databaseProvider)
    }

    @Provides
    fun provideCacheDataSource(cache: SimpleCache): CacheDataSource.Factory =
        CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory())
            .setCacheWriteDataSinkFactory(CacheDataSink.Factory().setCache(cache))
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR or CacheDataSource.FLAG_BLOCK_ON_CACHE)

    @Provides
    fun provideAudioAttributes(): AudioAttributes = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_SPEECH)
        .setUsage(C.USAGE_MEDIA)
        .build()

    @SuppressLint("UnsafeOptInUsageError")
    @Provides
    fun provideExoPlayer(
        @ApplicationContext context: Context,
        cacheDataSource: CacheDataSource.Factory,
        audioAttributes: AudioAttributes
    ): ExoPlayer = ExoPlayer.Builder(context)
        .setMediaSourceFactory(DefaultMediaSourceFactory(cacheDataSource))
        .setSeekForwardIncrementMs(Constants.UI.BookSummary.FAST_FORWARD_OFFSET_MILLIS)
        .setSeekBackIncrementMs(Constants.UI.BookSummary.REWIND_OFFSET_MILLIS)
        .setAudioAttributes(audioAttributes, PLAYER_HANDLE_FOCUS)
        .build().apply {
            playWhenReady = PLAYER_PLAY_WHEN_READY
            repeatMode = PLAYER_REPEAT_MODE
        }
}