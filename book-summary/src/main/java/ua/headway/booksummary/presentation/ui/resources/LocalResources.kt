package ua.headway.booksummary.presentation.ui.resources

import androidx.compose.ui.graphics.Color
import ua.headway.booksummary.R

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

    // TODO: Заменить везде на это
    object Strings {
        val NotificationChannelName = R.string.notification_channel_name
        val KeyPointTitle = R.string.key_point_title
    }

    object Colors {
        val Black = Color.Black
        val White = Color.White
        val Gray = Color.Gray
        val DarkGray = Color.DarkGray
        val LightGray = Color (0xFFF2EBE8)
        val MilkWhite = Color(0xFFF8F5F1)
        val Blue = Color (0xFF0066FF)
    }
}