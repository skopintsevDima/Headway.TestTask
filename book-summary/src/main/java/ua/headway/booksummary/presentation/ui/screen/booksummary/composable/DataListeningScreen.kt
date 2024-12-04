package ua.headway.booksummary.presentation.ui.screen.booksummary.composable

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.headway.booksummary.R
import ua.headway.booksummary.presentation.ui.resources.Constants.UI.BookSummary.FAST_FORWARD_OFFSET_MILLIS
import ua.headway.booksummary.presentation.ui.resources.Constants.UI.BookSummary.REWIND_OFFSET_MILLIS
import ua.headway.booksummary.presentation.ui.resources.LocalResources
import ua.headway.booksummary.presentation.ui.screen.booksummary.BookSummaryViewModel
import ua.headway.booksummary.presentation.ui.screen.booksummary.UiIntent
import ua.headway.booksummary.presentation.ui.screen.booksummary.UiState
import ua.headway.booksummary.presentation.ui.tool.formatTime

@Composable
fun DataListeningScreen(
    data: UiState.Data,
    viewModel: BookSummaryViewModel
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        DataLandscapeScreen(data, viewModel)
    } else {
        DataPortraitScreen(data, viewModel)
    }
}

@Composable
private fun DataPortraitScreen(data: UiState.Data, viewModel: BookSummaryViewModel) {
    val onToggleAudioSpeed = remember(viewModel) { { viewModel.tryHandleIntent(UiIntent.ToggleAudioSpeed) } }
    val onToggleAudio = remember(viewModel) { { viewModel.tryHandleIntent(UiIntent.ToggleAudio) } }
    val onPlaybackTimeChangeStarted = remember(viewModel) { { viewModel.tryHandleIntent(UiIntent.StartPlaybackPositionChange) } }
    val onPlaybackTimeChangeFinished = remember(viewModel) { { position: Float -> viewModel.tryHandleIntent(UiIntent.FinishPlaybackPositionChange(position.toLong())) } }
    val onRewind = remember(viewModel) { { viewModel.tryHandleIntent(UiIntent.ShiftAudioPosition(REWIND_OFFSET_MILLIS.unaryMinus())) } }
    val onFastForward = remember(viewModel) { { viewModel.tryHandleIntent(UiIntent.ShiftAudioPosition(FAST_FORWARD_OFFSET_MILLIS)) } }
    val onSkipBackward = remember(viewModel) { { viewModel.tryHandleIntent(UiIntent.GoPreviousPart) } }
    val onSkipForward = remember(viewModel) { { viewModel.tryHandleIntent(UiIntent.GoNextPart) } }
    val onAudioModeClick = remember(viewModel) { { viewModel.tryHandleIntent(UiIntent.ToggleSummaryMode) } }
    val onTextModeClick = remember(viewModel) { { viewModel.tryHandleIntent(UiIntent.ToggleSummaryMode) } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.background)
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
            playbackTimeMs = data.currentAudioPositionMs.toFloat(),
            totalTimeMs = data.currentAudioDurationMs.toFloat(),
            onPlaybackTimeChangeStarted = onPlaybackTimeChangeStarted,
            onPlaybackTimeChangeFinished = onPlaybackTimeChangeFinished
        )
        Spacer(modifier = Modifier.height(16.dp))

        PlaybackSpeedToggle(data.audioSpeedLevel, onToggleAudioSpeed)
        Spacer(modifier = Modifier.height(32.dp))

        PlaybackControls(
            isAudioPlaying = data.isAudioPlaying,
            onToggleAudio = onToggleAudio,
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
private fun DataLandscapeScreen(data: UiState.Data, viewModel: BookSummaryViewModel) {
    val onToggleAudioSpeed = remember(viewModel) { { viewModel.tryHandleIntent(UiIntent.ToggleAudioSpeed) } }
    val onToggleAudio = remember(viewModel) { { viewModel.tryHandleIntent(UiIntent.ToggleAudio) } }
    val onPlaybackTimeChangeStarted = remember(viewModel) { { viewModel.tryHandleIntent(UiIntent.StartPlaybackPositionChange) } }
    val onPlaybackTimeChangeFinished = remember(viewModel) { { position: Float -> viewModel.tryHandleIntent(UiIntent.FinishPlaybackPositionChange(position.toLong())) } }
    val onRewind = remember(viewModel) { { viewModel.tryHandleIntent(UiIntent.ShiftAudioPosition(REWIND_OFFSET_MILLIS.unaryMinus())) } }
    val onFastForward = remember(viewModel) { { viewModel.tryHandleIntent(UiIntent.ShiftAudioPosition(FAST_FORWARD_OFFSET_MILLIS)) } }
    val onSkipBackward = remember(viewModel) { { viewModel.tryHandleIntent(UiIntent.GoPreviousPart) } }
    val onSkipForward = remember(viewModel) { { viewModel.tryHandleIntent(UiIntent.GoNextPart) } }
    val onAudioModeClick = remember(viewModel) { { viewModel.tryHandleIntent(UiIntent.ToggleSummaryMode) } }
    val onTextModeClick = remember(viewModel) { { viewModel.tryHandleIntent(UiIntent.ToggleSummaryMode) } }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.background)
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
                .fillMaxHeight()
                .padding(start = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PartNumberTitle(data.currentPartIndex + 1, data.partsTotal)
            Spacer(modifier = Modifier.height(10.dp))

            PartDescription(data.currentSummaryPart.description)
            Spacer(modifier = Modifier.height(16.dp))

            PlaybackProgressBar(
                playbackTimeMs = data.currentAudioPositionMs.toFloat(),
                totalTimeMs = data.currentAudioDurationMs.toFloat(),
                onPlaybackTimeChangeStarted = onPlaybackTimeChangeStarted,
                onPlaybackTimeChangeFinished = onPlaybackTimeChangeFinished
            )
            Spacer(modifier = Modifier.height(16.dp))

            PlaybackSpeedToggle(data.audioSpeedLevel, onToggleAudioSpeed)
            Spacer(modifier = Modifier.height(16.dp))

            PlaybackControls(
                isAudioPlaying = data.isAudioPlaying,
                onToggleAudio = onToggleAudio,
                onRewind = onRewind,
                onFastForward = onFastForward,
                onSkipBackward = onSkipBackward,
                onSkipForward = onSkipForward
            )
            Spacer(modifier = Modifier.height(16.dp))

            SummaryModeToggle(
                isListeningModeEnabled = data.isListeningModeEnabled,
                onAudioModeClick = onAudioModeClick,
                onTextModeClick = onTextModeClick
            )
        }
    }
}

@Composable
private fun PlaybackProgressBar(
    playbackTimeMs: Float,
    totalTimeMs: Float,
    onPlaybackTimeChangeStarted: () -> Unit,
    onPlaybackTimeChangeFinished: (Float) -> Unit
) {
    val formattedPlaybackTime by remember(playbackTimeMs) {
        derivedStateOf { formatTime(playbackTimeMs) }
    }
    val formattedTotalTime by remember(totalTimeMs) {
        derivedStateOf { formatTime(totalTimeMs) }
    }
    val sliderValue = remember(playbackTimeMs) { mutableFloatStateOf(playbackTimeMs) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = formattedPlaybackTime,
            style = MaterialTheme.typography.caption.copy(
                color = MaterialTheme.colors.onBackground,
                fontWeight = FontWeight.Light,
                fontSize = 14.sp
            )
        )
        Slider(
            value = sliderValue.floatValue,
            onValueChange = {
                onPlaybackTimeChangeStarted()
                sliderValue.floatValue = it
            },
            onValueChangeFinished = {
                val newPlaybackTimeMs = sliderValue.floatValue
                onPlaybackTimeChangeFinished(newPlaybackTimeMs)
            },
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
                color = MaterialTheme.colors.onBackground,
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
    val playbackSpeedFormatted by remember(playbackSpeed) {
        derivedStateOf {
            when (playbackSpeed) {
                1f -> "1"
                1.5f -> "1.5"
                2f -> "2"
                else -> "1"
            }
        }
    }
    Button(
        onClick = onSpeedToggle,
        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.surface),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        contentPadding = PaddingValues(horizontal = 10.dp),
        modifier = Modifier
            .wrapContentWidth()
            .height(40.dp),
        elevation = ButtonDefaults.elevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = stringResource(R.string.speed, playbackSpeedFormatted),
            style = MaterialTheme.typography.button.copy(
                color = MaterialTheme.colors.onSurface,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                letterSpacing = 0.5.sp
            ),
        )
    }
}

@Composable
private fun PlaybackControls(
    isAudioPlaying: Boolean,
    onToggleAudio: () -> Unit,
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
                imageVector = ImageVector.vectorResource(LocalResources.Icons.SkipBack),
                contentDescription = "Skip Back",
                tint = MaterialTheme.colors.secondary
            )
        }

        IconButton(onClick = onRewind) {
            Icon(
                modifier = Modifier.size(48.dp),
                imageVector = ImageVector.vectorResource(LocalResources.Icons.Rewind5),
                contentDescription = "Rewind 5 seconds",
                tint = MaterialTheme.colors.secondary
            )
        }

        IconButton(onClick = onToggleAudio) {
            Icon(
                modifier = Modifier.size(64.dp),
                imageVector = ImageVector.vectorResource(LocalResources.Icons.Pause.takeIf {
                    isAudioPlaying
                } ?: LocalResources.Icons.Play),
                contentDescription = "Play/Pause",
                tint = MaterialTheme.colors.secondary
            )
        }

        IconButton(onClick = onFastForward) {
            Icon(
                modifier = Modifier.size(48.dp),
                imageVector = ImageVector.vectorResource(LocalResources.Icons.Forward10),
                contentDescription = "Fast Forward 10 Seconds",
                tint = MaterialTheme.colors.secondary
            )
        }

        IconButton(onClick = onSkipForward) {
            Icon(
                modifier = Modifier.size(48.dp),
                imageVector = ImageVector.vectorResource(LocalResources.Icons.SkipForward),
                contentDescription = "Skip Forward",
                tint = MaterialTheme.colors.secondary
            )
        }
    }
}