package ua.headway.booksummary.presentation.ui.resources

object Constants {
    object ErrorCodes {
        object BookSummary {
            const val ERROR_NO_DATA_FOR_PLAYER = 101
            const val ERROR_UNKNOWN = 100500
        }
    }

    object UI {
        const val CROSSFADE_ENABLED = true

        object BookSummary {
            const val AUDIO_SPEED_LEVEL_DEFAULT = 1f
            const val REWIND_OFFSET_MILLIS = 5 * 1000L
            const val FAST_FORWARD_OFFSET_MILLIS = 10 * 1000L
            const val DELAY_PLAYER_POSITION_UPDATES_MILLIS = 50L

            const val AUDIO_PLAYING_DEFAULT = false
            const val LISTENING_ENABLED_DEFAULT = true

            const val FORMAT_PLAYBACK_TIME = "%02d:%02d"
        }
    }
}