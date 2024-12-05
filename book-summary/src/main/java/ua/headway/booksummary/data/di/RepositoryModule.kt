package ua.headway.booksummary.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ua.headway.booksummary.data.di.source.DatabaseSource
import ua.headway.booksummary.data.di.source.MemorySource
import ua.headway.booksummary.data.di.source.NetworkSource
import ua.headway.booksummary.data.repository.BookRepositoryImpl
import ua.headway.booksummary.data.source.BookSource
import ua.headway.booksummary.domain.repository.BookRepository

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Provides
    fun provideBookRepository(
        @MemorySource memoryBookSource: BookSource,
        @NetworkSource networkBookSource: BookSource,
        @DatabaseSource dbBookSource: BookSource
    ): BookRepository = BookRepositoryImpl(
        memoryBookSource,
        networkBookSource,
        dbBookSource
    )
}