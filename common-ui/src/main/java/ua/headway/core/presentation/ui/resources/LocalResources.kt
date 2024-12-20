package ua.headway.core.presentation.ui.resources

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.headway.core.R

object LocalResources {
    object Icons {
        val Play = R.drawable.ic_play
        val Pause = R.drawable.ic_pause
        val SkipBack = R.drawable.ic_skip_back
        val SkipForward = R.drawable.ic_skip_forward
        val Rewind5 = R.drawable.ic_rewind_5
        val Forward10 = R.drawable.ic_forward_10
        val Headset = R.drawable.ic_headset
        val List = R.drawable.ic_list
        val Refresh = R.drawable.ic_refresh
    }

    object Images {
        val PlaceholderImage = R.drawable.placeholder_image
        val PlaceholderError = R.drawable.placeholder_error
    }

    object Strings {
        val NotificationChannelName = R.string.notification_channel_name
        val KeyPointTitle = R.string.key_point_title
        val IdleMessage = R.string.idle_message
        val Retry = R.string.retry
        val Okay = R.string.okay
        val UnknownErrorMessage = R.string.unknown_error_message_to_user
        val GlobalCrashMessage = R.string.global_crash_message_to_user
        val PermissionDeniedMessage = R.string.permission_denied_error_message_to_user
        val Speed = R.string.speed
        val SkipBack = R.string.content_description_skip_back
        val SkipForward = R.string.content_description_skip_forward

        val ErrorNoDataForPlayer = R.string.error_no_data_for_player
        val ErrorLoadBookData = R.string.error_load_book_data
        val ErrorPlayerInit = R.string.error_player_init
        val ErrorPlayback = R.string.error_playback
        val ErrorPlayerSeekFailed = R.string.error_player_seek_failed
        val ErrorPlayerToggleFailed = R.string.error_player_toggle_failed
        val ErrorPlayerSpeedChangeFailed = R.string.error_player_speed_change_failed
        val ErrorPlayerSkipFailed = R.string.error_player_skip_failed
        val ErrorUnknown = R.string.error_unknown
    }

    object Colors {
        @Stable
        val Black = Color.Black
        @Stable
        val White = Color.White
        @Stable
        val Gray = Color.Gray
        @Stable
        val DarkGray = Color.DarkGray
        @Stable
        val LightGray = Color(0xFFF2EBE8)
        @Stable
        val MilkWhite = Color(0xFFF8F5F1)
        @Stable
        val Blue = Color(0xFF0066FF)
    }

    object Dimensions {
        object Padding {
            val ExtraSmall = 4.dp
            val Small = 8.dp
            val Medium = 16.dp
            val Large = 24.dp
            val XLarge = 32.dp
            val XXLarge = 40.dp
            val XXXLarge = 48.dp

            val SummaryToggleBottomPortrait = 22.dp
            val SummaryToggleBottomLandscape = 16.dp
        }

        object Image {
            val Height = 320.dp
            val Width = 220.dp
            val CornerRadius = 8.dp
        }

        object Button {
            val HeightSmall = 40.dp
            val WidthSmall = 100.dp
        }

        object Icon {
            val ExtraLarge = 96.dp
            val Large = 48.dp
            val Medium = 40.dp
        }

        object Text {
            val SizeLarge = 24.sp
            val SizeMedium = 16.sp
            val SizeSmall = 14.sp
            val SizeTiny = 10.dp

            val HeightSmall = 20.dp
            val HeightMedium = 60.dp

            val SpacingLarge = 1.5.sp
            val SpacingSmall = 0.5.sp
        }

        object Size {
            val BorderWidth = 1.dp
            val ButtonCornerRadius = 8.dp

            object FillWidth {
                const val HALF = 0.5f
                const val LARGE = 0.8f
            }
        }
    }
}