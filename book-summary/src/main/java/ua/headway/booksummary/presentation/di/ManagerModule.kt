package ua.headway.booksummary.presentation.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ua.headway.booksummary.domain.interactor.AudioPlaybackInteractor
import ua.headway.booksummary.presentation.manager.AudioPlaybackInteractorImpl
import ua.headway.booksummary.presentation.manager.BookSummaryPlayerSetupManager
import ua.headway.booksummary.presentation.manager.PlayerSetupManager

@Module
@InstallIn(ViewModelComponent::class)
class ManagerModule {
    @Provides
    fun provideAudioPlaybackInteractor(): AudioPlaybackInteractor = AudioPlaybackInteractorImpl()

    @Provides
    fun providePlayerSetupManager(): PlayerSetupManager = BookSummaryPlayerSetupManager()
}