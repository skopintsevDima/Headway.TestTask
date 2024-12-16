package ua.headway.booksummary.presentation.ui.screen.booksummary.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import ua.headway.booksummary.presentation.ui.resources.LocalResources
import ua.headway.booksummary.presentation.ui.screen.booksummary.BookSummaryViewModel
import ua.headway.booksummary.presentation.ui.screen.booksummary.UiIntent
import ua.headway.booksummary.presentation.ui.screen.booksummary.UiState

@Composable
fun DataReadingScreen(
    data: UiState.Data,
    viewModel: BookSummaryViewModel,
    modifier: Modifier,
    modeTogglePadding: Dp
) {
    val scrollState = rememberScrollState()
    val onAudioModeClick = remember(viewModel) { { viewModel.tryHandleIntent(UiIntent.ToggleSummaryMode) } }
    val onTextModeClick = remember(viewModel) { { viewModel.tryHandleIntent(UiIntent.ToggleSummaryMode) } }
    val onSkipBack = remember(viewModel) { { viewModel.tryHandleIntent(UiIntent.GoPreviousPart) } }
    val onSkipForward = remember(viewModel) { { viewModel.tryHandleIntent(UiIntent.GoNextPart) } }

    val isFirstPartNow = remember(data) { data.isFirstPartNow }
    val isLastPartNow = remember(data) { data.isLastPartNow }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PartNumberTitle(data.currentPartIndex + 1, data.partsTotal)
        Spacer(modifier = Modifier.height(LocalResources.Dimensions.Padding.Small))

        PartDescription(data.currentSummaryPart.description)
        Spacer(modifier = Modifier.height(LocalResources.Dimensions.Padding.Medium))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = LocalResources.Dimensions.Padding.Medium)
        ) {
            Text(
                text = data.currentSummaryPart.text,
                style = MaterialTheme.typography.body1.copy(
                    color = MaterialTheme.colors.onBackground,
                    fontSize = LocalResources.Dimensions.Text.SizeMedium,
                    lineHeight = LocalResources.Dimensions.Text.SizeLarge
                ),
                textAlign = TextAlign.Start
            )
        }

        Spacer(modifier = Modifier.height(LocalResources.Dimensions.Padding.Medium))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LocalResources.Dimensions.Padding.Medium)
        ) {
            IconButton(
                onClick = onSkipBack,
                enabled = !isFirstPartNow
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(LocalResources.Icons.SkipBack),
                    contentDescription = stringResource(LocalResources.Strings.SkipBack),
                    modifier = Modifier.size(LocalResources.Dimensions.Icon.Medium),
                    tint = if (!isFirstPartNow) MaterialTheme.colors.secondary else MaterialTheme.colors.secondary.copy(alpha = 0.4f)
                )
            }

            SummaryModeToggle(
                isListeningModeEnabled = data.isListeningModeEnabled,
                onAudioModeClick = onAudioModeClick,
                onTextModeClick = onTextModeClick
            )

            IconButton(
                onClick = onSkipForward,
                enabled = !isLastPartNow
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(LocalResources.Icons.SkipForward),
                    contentDescription = stringResource(LocalResources.Strings.SkipForward),
                    modifier = Modifier.size(LocalResources.Dimensions.Icon.Medium),
                    tint = if (!isLastPartNow) MaterialTheme.colors.secondary else MaterialTheme.colors.secondary.copy(alpha = 0.4f)
                )
            }
        }
        Spacer(modifier = Modifier.height(modeTogglePadding))
    }
}
