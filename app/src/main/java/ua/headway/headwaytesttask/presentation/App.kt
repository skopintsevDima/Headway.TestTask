package ua.headway.headwaytesttask.presentation

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import ua.headway.booksummary.presentation.audio.AudioPlaybackService
import ua.headway.core.presentation.ui.resources.LocalResources
import ua.headway.headwaytesttask.presentation.ui.activity.CrashActivity
import kotlin.system.exitProcess

@HiltAndroidApp
class App: Application() {
    override fun onCreate() {
        super.onCreate()
        createAudioPlaybackNotificationChannel()
        setupGlobalExceptionHandler()
    }

    private fun createAudioPlaybackNotificationChannel() {
        val channel = NotificationChannel(
            AudioPlaybackService.NOTIFICATION_CHANNEL_ID,
            getString(LocalResources.Strings.NotificationChannelName),
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun setupGlobalExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("GlobalException", "Uncaught exception on thread ${thread.name}: $throwable")

            val intent = CrashActivity.newIntent(this)
            startActivity(intent)

            exitProcess(1)
        }
    }
}