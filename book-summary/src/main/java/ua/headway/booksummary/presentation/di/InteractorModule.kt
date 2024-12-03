package ua.headway.booksummary.presentation.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ua.headway.booksummary.domain.interactor.AudioPlaybackInteractor
import ua.headway.booksummary.presentation.interactor.AudioPlaybackInteractorImpl

@Module
@InstallIn(ViewModelComponent::class)
class InteractorModule {
    @Provides
    fun provideAudioPlaybackInteractor(): AudioPlaybackInteractor = AudioPlaybackInteractorImpl()
}