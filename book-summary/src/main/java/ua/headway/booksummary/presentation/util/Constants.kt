package ua.headway.booksummary.presentation.util

import androidx.media3.common.Player

object Constants {
    object ErrorCodes {
        object BookSummary {
            const val ERROR_NO_DATA_FOR_PLAYER = 101
            const val ERROR_LOAD_BOOK_DATA = 102
            const val ERROR_PLAYER_INIT = 103
            const val ERROR_PLAYER_PLAYBACK = 104
            const val ERROR_PLAYER_SEEK_FAILED = 105
            const val ERROR_PLAYER_TOGGLE_FAILED = 106
            const val ERROR_PLAYER_SPEED_CHANGE_FAILED = 107
            const val ERROR_PLAYER_SKIP_FAILED = 108
            const val ERROR_PLAYER_TEMPORARILY_UNAVAILABLE = 109
            const val ERROR_UNKNOWN = 100500
        }
    }

    object UI {
        object BookSummary {
            const val TAG = "BookSummary"

            const val AUDIO_SPEED_LEVEL_DEFAULT = 1f
            const val REWIND_OFFSET_MILLIS = 5 * 1000L
            const val FAST_FORWARD_OFFSET_MILLIS = 10 * 1000L
            const val DELAY_PLAYER_SYNC_MILLIS = 50L
            const val PLAYER_CACHE_SIZE_MB = 50L * 1024 * 1024

            const val AUDIO_PLAYING_DEFAULT = false
            const val LISTENING_ENABLED_DEFAULT = true
            const val PLAYER_HANDLE_FOCUS = true
            const val PLAYER_PLAY_WHEN_READY = true
            const val PLAYER_REPEAT_MODE = Player.REPEAT_MODE_OFF

            const val FORMAT_PLAYBACK_TIME = "%02d:%02d"

            const val AUDIO_CACHE_DIR_NAME = "book_summary_audio_cache"
        }
    }
}