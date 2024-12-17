package ua.headway.booksummary.presentation.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(ViewModelComponent::class)
class DispatchersModule {
    @Provides
    fun provideBackgroundOpsDispatcher(): CoroutineDispatcher = Dispatchers.Default
}