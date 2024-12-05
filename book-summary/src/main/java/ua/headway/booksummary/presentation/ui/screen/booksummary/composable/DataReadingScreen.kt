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
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBookCover(data.bookCoverUrl)
        Spacer(modifier = Modifier.height(24.dp))

        PartNumberTitle(data.currentPartIndex + 1, data.partsTotal)
        Spacer(modifier = Modifier.height(10.dp))

        PartDescription(data.currentSummaryPart.description)
        Spacer(modifier = Modifier.height(16.dp))

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
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                ),
                textAlign = TextAlign.Start
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onSkipBack) {
                Icon(
                    imageVector = ImageVector.vectorResource(LocalResources.Icons.SkipBack),
                    contentDescription = "Skip Back",
                    tint = MaterialTheme.colors.secondary,
                    modifier = Modifier.size(48.dp)
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
                    contentDescription = "Skip Forward",
                    tint = MaterialTheme.colors.secondary,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(48.dp))
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
            .padding(16.dp)
    ) {
        TopBookCover(
            bookCoverUrl = data.bookCoverUrl,
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)
        )

        Column(
            modifier = Modifier
                .weight(2f)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PartNumberTitle(data.currentPartIndex + 1, data.partsTotal)
            Spacer(modifier = Modifier.height(10.dp))

            PartDescription(data.currentSummaryPart.description)
            Spacer(modifier = Modifier.height(16.dp))

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
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    ),
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onSkipBack) {
                    Icon(
                        imageVector = ImageVector.vectorResource(LocalResources.Icons.SkipBack),
                        contentDescription = "Skip Back",
                        tint = MaterialTheme.colors.secondary,
                        modifier = Modifier.size(48.dp)
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
                        contentDescription = "Skip Forward",
                        tint = MaterialTheme.colors.secondary,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    }
}