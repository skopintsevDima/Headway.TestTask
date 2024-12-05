package ua.headway.booksummary.data.di.source

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ua.headway.booksummary.data.api.ApiBookSource
import ua.headway.booksummary.data.source.BookSource
import ua.headway.booksummary.data.db.DbBookSource
import ua.headway.booksummary.data.memory.MemoryBookSource

@Module
@InstallIn(SingletonComponent::class)
class DataSourceModule {
    @Provides
    @MemorySource
    fun provideMemoryBookSource(): BookSource = MemoryBookSource()

    @Provides
    @NetworkSource
    fun provideNetworkBookSource(
        @ApplicationContext context: Context
    ): BookSource = ApiBookSource(context)

    @Provides
    @DatabaseSource
    fun provideDatabaseBookSource(): BookSource = DbBookSource()
}