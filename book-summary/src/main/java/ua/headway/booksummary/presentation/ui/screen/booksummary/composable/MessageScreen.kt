package ua.headway.booksummary.presentation.ui.screen.booksummary.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import ua.headway.booksummary.presentation.ui.screen.booksummary.TestTags.TAG_MESSAGE_SCREEN
import ua.headway.core.presentation.ui.resources.LocalResources

@Composable
fun MessageScreen(message: String) {
    Box(
        modifier = Modifier
            .testTag(TAG_MESSAGE_SCREEN)
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(LocalResources.Dimensions.Padding.Medium),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.body1.copy(
                color = MaterialTheme.colors.onBackground,
                fontSize = LocalResources.Dimensions.Text.SizeLarge
            ),
            textAlign = TextAlign.Center
        )
    }
}