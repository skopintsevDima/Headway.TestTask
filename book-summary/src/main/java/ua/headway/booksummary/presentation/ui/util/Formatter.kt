package ua.headway.booksummary.presentation.ui.util

import androidx.compose.ui.text.intl.Locale
import ua.headway.booksummary.presentation.util.Constants.UI.BookSummary.FORMAT_PLAYBACK_TIME

fun formatTime(milliseconds: Float): String {
    val timeMs = milliseconds.coerceAtLeast(0f)

    val minutes = (timeMs / 60000).toInt()
    val seconds = ((timeMs % 60000) / 1000).toInt()
    return String.format(
        Locale.current.platformLocale,
        FORMAT_PLAYBACK_TIME,
        minutes,
        seconds
    )
}