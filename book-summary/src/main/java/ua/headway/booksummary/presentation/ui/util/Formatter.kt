package ua.headway.booksummary.presentation.ui.util

import androidx.compose.ui.text.intl.Locale
import ua.headway.booksummary.presentation.ui.resources.Constants.UI.BookSummary.FORMAT_PLAYBACK_TIME

fun formatTime(milliseconds: Float): String {
    val minutes = (milliseconds / 60000).toInt()
    val seconds = ((milliseconds % 60000) / 1000).toInt()
    return String.format(
        Locale.current.platformLocale,
        FORMAT_PLAYBACK_TIME,
        minutes,
        seconds
    )
}