package ua.headway.booksummary.presentation.ui.screen.booksummary.composable

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import ua.headway.booksummary.presentation.ui.resources.LocalResources
import ua.headway.booksummary.presentation.ui.screen.booksummary.BookSummaryViewModel
import ua.headway.booksummary.presentation.ui.screen.booksummary.UiIntent
import ua.headway.booksummary.presentation.ui.screen.booksummary.UiState

@Composable
fun DataReadingScreen(
    data: UiState.Data,
    viewModel: BookSummaryViewModel,
    onSkipBack: () -> Unit,
    onSkipForward: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        DataReadingLandscapeScreen(data, viewModel, onSkipBack, onSkipForward)
    } else {
        DataReadingPortraitScreen(data, viewModel, onSkipBack, onSkipForward)
    }
}

@Composable
fun DataReadingPortraitScreen(
    data: UiState.Data,
    viewModel: BookSummaryViewModel,
    onSkipBack: () -> Unit,
    onSkipForward: () -> Unit
) {
    val scrollState = rememberScrollState()
    val onAudioModeClick = remember(viewModel) { { viewModel.tryHandleIntent(UiIntent.ToggleSummaryMode) } }
    val onTextModeClick = remember(viewModel) { { viewModel.tryHandleIntent(UiIntent.ToggleSummaryMode) } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(LocalResources.Dimensions.Padding.Medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBookCover(data.bookCoverUrl)
        Spacer(modifier = Modifier.height(LocalResources.Dimensions.Padding.Large))

        PartNumberTitle(data.currentPartIndex + 1, data.partsTotal)
        Spacer(modifier = Modifier.height(LocalResources.Dimensions.Padding.Small))

        PartDescription(data.currentSummaryPart.description)
        Spacer(modifier = Modifier.height(LocalResources.Dimensions.Padding.Medium))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
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
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onSkipBack) {
                Icon(
                    imageVector = ImageVector.vectorResource(LocalResources.Icons.SkipBack),
                    contentDescription = stringResource(LocalResources.Strings.SkipBack),
                    tint = MaterialTheme.colors.secondary,
                    modifier = Modifier.size(LocalResources.Dimensions.Icon.Medium)
                )
            }

            SummaryModeToggle(
                isListeningModeEnabled = data.isListeningModeEnabled,
                onAudioModeClick = onAudioModeClick,
                onTextModeClick = onTextModeClick
            )

            IconButton(onClick = onSkipForward) {
                Icon(
                    imageVector = ImageVector.vectorResource(LocalResources.Icons.SkipForward),
                    contentDescription = stringResource(LocalResources.Strings.SkipForward),
                    tint = MaterialTheme.colors.secondary,
                    modifier = Modifier.size(LocalResources.Dimensions.Icon.Medium)
                )
            }
        }
        Spacer(modifier = Modifier.height(LocalResources.Dimensions.Padding.ExtraLarge))
    }
}

@Composable
fun DataReadingLandscapeScreen(
    data: UiState.Data,
    viewModel: BookSummaryViewModel,
    onSkipBack: () -> Unit,
    onSkipForward: () -> Unit
) {
    val scrollState = rememberScrollState()
    val onAudioModeClick = remember(viewModel) { { viewModel.tryHandleIntent(UiIntent.ToggleSummaryMode) } }
    val onTextModeClick = remember(viewModel) { { viewModel.tryHandleIntent(UiIntent.ToggleSummaryMode) } }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(LocalResources.Dimensions.Padding.Medium)
    ) {
        TopBookCover(
            bookCoverUrl = data.bookCoverUrl,
            modifier = Modifier
                .weight(1f)
                .padding(end = LocalResources.Dimensions.Padding.Medium)
        )

        Column(
            modifier = Modifier
                .weight(2f)
                .fillMaxHeight(),
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
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onSkipBack) {
                    Icon(
                        imageVector = ImageVector.vectorResource(LocalResources.Icons.SkipBack),
                        contentDescription = stringResource(LocalResources.Strings.SkipBack),
                        tint = MaterialTheme.colors.secondary,
                        modifier = Modifier.size(LocalResources.Dimensions.Icon.Medium)
                    )
                }

                SummaryModeToggle(
                    isListeningModeEnabled = data.isListeningModeEnabled,
                    onAudioModeClick = onAudioModeClick,
                    onTextModeClick = onTextModeClick
                )

                IconButton(onClick = onSkipForward) {
                    Icon(
                        imageVector = ImageVector.vectorResource(LocalResources.Icons.SkipForward),
                        contentDescription = stringResource(LocalResources.Strings.SkipForward),
                        tint = MaterialTheme.colors.secondary,
                        modifier = Modifier.size(LocalResources.Dimensions.Icon.Medium)
                    )
                }
            }
        }
    }
}