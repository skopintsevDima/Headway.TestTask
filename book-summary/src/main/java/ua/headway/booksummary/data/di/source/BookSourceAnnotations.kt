package ua.headway.booksummary.data.di.source

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MemorySource

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NetworkSource

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DatabaseSource
