package ua.headway.booksummary.presentation.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ua.headway.booksummary.domain.repository.BookRepository
import ua.headway.booksummary.domain.usecase.GetBookSummaryUseCase
import ua.headway.booksummary.presentation.usecase.GetBookSummaryUseCaseImpl

@Module
@InstallIn(ViewModelComponent::class)
class UseCaseModule {
    @Provides
    fun provideGetBookSummaryUseCase(
        bookRepository: BookRepository
    ): GetBookSummaryUseCase = GetBookSummaryUseCaseImpl(bookRepository)
}