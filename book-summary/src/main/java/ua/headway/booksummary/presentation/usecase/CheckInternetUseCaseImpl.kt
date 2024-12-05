package ua.headway.booksummary.presentation.usecase

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import ua.headway.booksummary.domain.usecase.CheckInternetUseCase
import ua.headway.booksummary.presentation.util.isNetworkAvailable

class CheckInternetUseCaseImpl(
    @ApplicationContext private val context: Context
): CheckInternetUseCase {
    override fun execute(): Boolean = context.isNetworkAvailable()
}