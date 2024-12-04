package ua.headway.booksummary.data.di.source

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ua.headway.booksummary.data.source.api.ApiBookSource
import ua.headway.booksummary.data.source.base.BookSource
import ua.headway.booksummary.data.source.db.DbBookSource
import ua.headway.booksummary.data.source.memory.MemoryBookSource

@Module
@InstallIn(SingletonComponent::class)
class DataSourceModule {
    @Provides
    @MemorySource
    fun provideMemoryBookSource(): BookSource = MemoryBookSource()

    @Provides
    @NetworkSource
    fun provideNetworkBookSource(): BookSource = ApiBookSource()

    @Provides
    @DatabaseSource
    fun provideDatabaseBookSource(): BookSource = DbBookSource()
}