package ua.headway.core.presentation.ui.util

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.content.res.Configuration.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "Landscape Dark",
    showBackground = true,
    heightDp = 480,
    widthDp = 960,
    uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL
)
@Preview(
    name = "Landscape Light",
    showBackground = true,
    heightDp = 480,
    widthDp = 960,
)
@Preview(
    name = "Portrait Dark",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL
)
@Preview(
    name = "Portrait Light",
    showBackground = true,
)
annotation class PreviewLightDarkBothOrientations