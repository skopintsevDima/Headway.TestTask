package ua.headway.booksummary.presentation.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ua.headway.booksummary.domain.repository.BookRepository
import ua.headway.booksummary.domain.usecase.CheckInternetUseCase
import ua.headway.booksummary.domain.usecase.GetBookSummaryUseCase
import ua.headway.booksummary.presentation.usecase.CheckInternetUseCaseImpl
import ua.headway.booksummary.presentation.usecase.GetBookSummaryUseCaseImpl

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {
    @Provides
    fun provideGetBookSummaryUseCase(
        bookRepository: BookRepository
    ): GetBookSummaryUseCase = GetBookSummaryUseCaseImpl(bookRepository)

    @Provides
    fun provideCheckInternetUseCase(
        @ApplicationContext context: Context
    ): CheckInternetUseCase = CheckInternetUseCaseImpl(context)
}