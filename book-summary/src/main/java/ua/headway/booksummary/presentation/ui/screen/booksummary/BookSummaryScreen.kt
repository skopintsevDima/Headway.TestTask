package ua.headway.booksummary.presentation.ui.screen.booksummary

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import ua.headway.booksummary.R
import ua.headway.booksummary.presentation.ui.resources.Constants.UI.BookSummary.FAST_FORWARD_OFFSET_MILLIS
import ua.headway.booksummary.presentation.ui.resources.Constants.UI.BookSummary.FORMAT_PLAYBACK_TIME
import ua.headway.booksummary.presentation.ui.resources.Constants.UI.BookSummary.REWIND_OFFSET_MILLIS
import ua.headway.booksummary.presentation.ui.resources.LocalResources

// TODO: (EVERYWHERE) Remove all magic constants into Constants + texts to Strings
// TODO: Remove MaterialTheme import ---> Apply theme from app module
@Composable
fun BookSummaryScreen(viewModel: BookSummaryViewModel = hiltViewModel()) {
    val uiState = viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.handleIntent(UiIntent.FetchBookSummary)
    }

    when (val stateValue = uiState.value) {
        UiState.Loading -> LoadingScreen()
        is UiState.Error -> ErrorScreen(stateValue)
        is UiState.Data -> DataScreen(stateValue, viewModel)
    }
}

@Composable
private fun LoadingScreen() {
    // TODO: Add shimmer
}

@Composable
private fun ErrorScreen(error: UiState.Error) {
    // TODO: Handle errors more visually accurate?
    val snackbarHostState = remember { SnackbarHostState() }

    val errorMessage = error.errorMsg
    val actionLabel = stringResource(R.string.oki_doki)
    LaunchedEffect(errorMessage) {
        snackbarHostState.showSnackbar(
            message = errorMessage,
            actionLabel = actionLabel
        )
    }

    SnackbarHost(
        hostState = snackbarHostState,
        snackbar = { snackbarData ->
            Snackbar(
                action = {
                    snackbarData.actionLabel?.let { actionLabel ->
                        TextButton(onClick = { snackbarData.dismiss() }) {
                            Text(text = actionLabel, color = LocalResources.Colors.Blue)
                        }
                    }
                }
            ) {
                Text(text = snackbarData.message)
            }
        }
    )
}

@Composable
private fun DataScreen(data: UiState.Data, viewModel: BookSummaryViewModel) {
    val onSetAudioPosition = remember(viewModel) { { position: Float -> viewModel.handleIntent(UiIntent.SetAudioPosition(position)) } }
    val onToggleAudioSpeed = remember(viewModel) { { viewModel.handleIntent(UiIntent.ToggleAudioSpeed) } }
    val onTogglePlay = remember(viewModel) { { viewModel.handleIntent(UiIntent.ToggleAudio) } }
    val onRewind = remember(viewModel) { { viewModel.handleIntent(UiIntent.ChangeAudioPosition(REWIND_OFFSET_MILLIS)) } }
    val onFastForward = remember(viewModel) { { viewModel.handleIntent(UiIntent.ChangeAudioPosition(FAST_FORWARD_OFFSET_MILLIS)) } }
    val onSkipBackward = remember(viewModel) { { viewModel.handleIntent(UiIntent.GoPreviousPart) } }
    val onSkipForward = remember(viewModel) { { viewModel.handleIntent(UiIntent.GoNextPart) } }
    val onAudioModeClick = remember(viewModel) { { viewModel.handleIntent(UiIntent.ToggleSummaryMode) } }
    val onTextModeClick = remember(viewModel) { { viewModel.handleIntent(UiIntent.ToggleSummaryMode) } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = LocalResources.Colors.MilkWhite)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBookCover(data.bookCoverUrl)
        Spacer(modifier = Modifier.height(48.dp))

        PartNumberTitle(data.currentPartIndex + 1, data.partsTotal)
        Spacer(modifier = Modifier.height(10.dp))

        PartDescription(data.currentSummaryPart.description)
        Spacer(modifier = Modifier.height(16.dp))

        PlaybackProgressBar(
            data.currentAudioPositionMs,
            data.currentSummaryPart.audioDurationMs,
            onSetAudioPosition
        )
        Spacer(modifier = Modifier.height(16.dp))

        PlaybackSpeedToggle(data.audioSpeedLevel, onToggleAudioSpeed)
        Spacer(modifier = Modifier.height(32.dp))

        PlaybackControls(
            isAudioPlaying = data.isAudioPlaying,
            onTogglePlay = onTogglePlay,
            onRewind = onRewind,
            onFastForward = onFastForward,
            onSkipBackward = onSkipBackward,
            onSkipForward = onSkipForward
        )

        SummaryModeToggle(
            isListeningModeEnabled = data.isListeningModeEnabled,
            onAudioModeClick = onAudioModeClick,
            onTextModeClick = onTextModeClick
        )
    }
}

@Composable
private fun TopBookCover(bookCoverUrl: String) {
    // TODO: Add placeholder image for: loading, error
    Box(
        modifier = Modifier
            .height(350.dp)
            .width(250.dp)
            .padding(top = 32.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = bookCoverUrl),
            contentDescription = "Book Cover",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun PartNumberTitle(partNumber: Int, partsTotal: Int) {
    Text(
        text = stringResource(R.string.key_point_title, partNumber, partsTotal),
        style = MaterialTheme.typography.body1.copy(
            color = LocalResources.Colors.Gray,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.5.sp
        )
    )
}

@Composable
private fun PartDescription(currentPartDescription: String) {
    Text(
        modifier = Modifier.padding(horizontal = 8.dp),
        text = currentPartDescription,
        style = MaterialTheme.typography.body1.copy(
            fontWeight = FontWeight.Normal,
            color = LocalResources.Colors.Black
        ),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun PlaybackProgressBar(
    playbackTimeMs: Float,
    totalTimeMs: Float,
    onPlaybackTimeChange: (Float) -> Unit
) {
    val formattedPlaybackTime by remember(playbackTimeMs) {
        derivedStateOf { formatTime(playbackTimeMs) }
    }
    val formattedTotalTime by remember(totalTimeMs) {
        derivedStateOf { formatTime(totalTimeMs) }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = formattedPlaybackTime,
            style = MaterialTheme.typography.caption.copy(
                color = LocalResources.Colors.Gray,
                fontWeight = FontWeight.Light,
                fontSize = 14.sp
            )
        )
        Slider(
            value = playbackTimeMs,
            onValueChange = onPlaybackTimeChange,
            valueRange = 0f..totalTimeMs,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 2.dp),
            colors = SliderDefaults.colors(
                thumbColor = LocalResources.Colors.Blue,
                activeTrackColor = LocalResources.Colors.Blue,
                inactiveTrackColor = LocalResources.Colors.LightGray
            )
        )
        Text(
            text = formattedTotalTime,
            style = MaterialTheme.typography.caption.copy(
                color = LocalResources.Colors.Gray,
                fontWeight = FontWeight.Light,
                fontSize = 14.sp
            )
        )
    }
}

@Composable
private fun PlaybackSpeedToggle(
    playbackSpeed: Float,
    onSpeedToggle: () -> Unit
) {
    Button(
        onClick = onSpeedToggle,
        colors = ButtonDefaults.buttonColors(backgroundColor = LocalResources.Colors.LightGray),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        contentPadding = PaddingValues(horizontal = 10.dp),
        modifier = Modifier
            .wrapContentWidth()
            .height(40.dp),
        elevation = ButtonDefaults.elevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = stringResource(R.string.speed, playbackSpeed),
            style = MaterialTheme.typography.button.copy(
                color = LocalResources.Colors.Black,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
        )
    }
}

@Composable
private fun PlaybackControls(
    isAudioPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onRewind: () -> Unit,
    onFastForward: () -> Unit,
    onSkipBackward: () -> Unit,
    onSkipForward: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onSkipBackward) {
            Icon(
                modifier = Modifier.size(48.dp),
                painter = painterResource(id = LocalResources.Icons.SkipBack),
                contentDescription = "Skip Back",
                tint = LocalResources.Colors.Black
            )
        }

        IconButton(onClick = onRewind) {
            Icon(
                modifier = Modifier.size(48.dp),
                painter = painterResource(id = LocalResources.Icons.Replay5),
                contentDescription = "Rewind 5 seconds",
                tint = LocalResources.Colors.Black
            )
        }

        IconButton(onClick = onTogglePlay) {
            Icon(
                modifier = Modifier.size(64.dp),
                painter = painterResource(id = LocalResources.Icons.Pause.takeIf {
                    isAudioPlaying
                } ?: LocalResources.Icons.Play),
                contentDescription = "Play/Pause",
                tint = LocalResources.Colors.Black
            )
        }

        IconButton(onClick = onFastForward) {
            Icon(
                modifier = Modifier.size(48.dp),
                painter = painterResource(id = LocalResources.Icons.Forward10),
                contentDescription = "Fast Forward 10 Seconds",
                tint = LocalResources.Colors.Black
            )
        }

        IconButton(onClick = onSkipForward) {
            Icon(
                modifier = Modifier.size(48.dp),
                painter = painterResource(id = LocalResources.Icons.SkipForward),
                contentDescription = "Skip Forward",
                tint = LocalResources.Colors.Black
            )
        }
    }
}

@Composable
private fun SummaryModeToggle(
    isListeningModeEnabled: Boolean,
    onAudioModeClick: () -> Unit,
    onTextModeClick: () -> Unit
) {
    val rowShape = RoundedCornerShape(50)
    Row(
        modifier = Modifier
            .padding(top = 32.dp)
            .clip(rowShape)
            .background(LocalResources.Colors.White)
            .border(width = 1.dp, color = LocalResources.Colors.LightGray, shape = rowShape),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ModeToggleIconButton(
            isSelected = isListeningModeEnabled,
            onClick = onAudioModeClick,
            icon = ImageVector.vectorResource(LocalResources.Icons.Headset),
            description = "Summary audio"
        )

        ModeToggleIconButton(
            isSelected = !isListeningModeEnabled,
            onClick = onTextModeClick,
            icon = ImageVector.vectorResource(LocalResources.Icons.List),
            description = "Summary text"
        )
    }
}

@Composable
private fun ModeToggleIconButton(
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    description: String
) {
    IconButton(
        onClick = onClick,
        enabled = !isSelected,
        modifier = Modifier
            .padding(4.dp)
            .wrapContentWidth()
            .wrapContentHeight()
            .background(
                color = LocalResources.Colors.Blue.takeIf { isSelected }
                    ?: LocalResources.Colors.White,
                shape = RoundedCornerShape(50)
            )
    ) {
        Icon(
            modifier = Modifier.padding(16.dp),
            imageVector = icon,
            contentDescription = description,
            tint = LocalResources.Colors.White.takeIf { isSelected } ?: LocalResources.Colors.Black,
        )
    }
}

private fun formatTime(milliseconds: Float): String {
    val minutes = (milliseconds / 60000).toInt()
    val seconds = ((milliseconds % 60000) / 1000).toInt()
    return String.format(
        Locale.current.platformLocale,
        FORMAT_PLAYBACK_TIME,
        minutes,
        seconds
    )
}

@Preview(showBackground = true)
@Composable
private fun BookSummaryPreview() {
    MaterialTheme {
        BookSummaryScreen()
    }
}
