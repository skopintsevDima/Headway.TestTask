package ua.headway.headwaytesttask.presentation

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import dagger.hilt.android.HiltAndroidApp
import ua.headway.booksummary.presentation.audio.AudioPlaybackService
import ua.headway.booksummary.presentation.ui.resources.LocalResources

@HiltAndroidApp
class App: Application() {
    override fun onCreate() {
        super.onCreate()
        createAudioPlaybackNotificationChannel()
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
}