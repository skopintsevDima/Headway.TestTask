package ua.headway.booksummary.presentation.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ua.headway.booksummary.presentation.ui.resources.provider.DefaultResourceProvider
import ua.headway.booksummary.presentation.ui.resources.provider.ResourceProvider

@Module
@InstallIn(SingletonComponent::class)
class ResourcesModule {
    @Provides
    fun provideResourceProvider(@ApplicationContext context: Context): ResourceProvider =
        DefaultResourceProvider(context)
}