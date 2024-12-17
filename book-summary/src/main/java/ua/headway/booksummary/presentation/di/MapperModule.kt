package ua.headway.booksummary.presentation.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ua.headway.booksummary.presentation.ui.screen.booksummary.mapper.BookSummaryUiStateMapper
import ua.headway.booksummary.presentation.ui.screen.booksummary.mapper.DefaultBookSummaryUiStateMapper
import ua.headway.core.presentation.ui.resources.provider.ResourceProvider

@Module
@InstallIn(ViewModelComponent::class)
class MapperModule {
    @Provides
    fun provideAudioPlaybackInteractor(
        resourceProvider: ResourceProvider
    ): BookSummaryUiStateMapper = DefaultBookSummaryUiStateMapper(resourceProvider)
}