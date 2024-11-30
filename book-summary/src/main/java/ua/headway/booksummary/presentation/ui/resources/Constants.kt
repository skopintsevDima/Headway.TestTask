package ua.headway.booksummary.presentation.ui.resources

object Constants {
    object ErrorCodes {
        object BookSummary {
            const val ERROR_SUMMARY_PARTS_ARE_OVER = 101
            const val ERROR_UNKNOWN = 100500
        }
    }

    object UI {
        object BookSummary {
            const val AUDIO_SPEED_LEVEL_DEFAULT = 1f
            const val REWIND_OFFSET_MILLIS = -5f * 1000
            const val FAST_FORWARD_OFFSET_MILLIS = 10f * 1000

            const val AUDIO_PLAYING_DEFAULT = false
            const val LISTENING_ENABLED_DEFAULT = true

            const val FORMAT_PLAYBACK_TIME = "%02d:%02d"
        }
    }
}