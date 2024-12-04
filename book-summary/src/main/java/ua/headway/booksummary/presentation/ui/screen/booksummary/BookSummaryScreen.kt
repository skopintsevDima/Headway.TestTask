package ua.headway.booksummary.presentation.ui.screen.booksummary

import android.content.res.Configuration
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
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
import ua.headway.booksummary.presentation.ui.composable.RequestPermission
import ua.headway.booksummary.presentation.ui.resources.Constants.UI.BookSummary.FAST_FORWARD_OFFSET_MILLIS
import ua.headway.booksummary.presentation.ui.resources.Constants.UI.BookSummary.FORMAT_PLAYBACK_TIME
import ua.headway.booksummary.presentation.ui.resources.Constants.UI.BookSummary.REWIND_OFFSET_MILLIS
import ua.headway.booksummary.presentation.ui.resources.LocalResources

// TODO: (EVERYWHERE) Remove all magic constants into Constants + texts to Strings
// TODO: Remove MaterialTheme import ---> Apply theme from app module
@Composable
fun BookSummaryScreen(viewModel: BookSummaryViewModel = hiltViewModel()) {
    InitWithPermissions(viewModel)

    val uiState =  viewModel.uiState.collectAsState()
    when (val data = uiState.value) {
        UiState.Idle -> IdleScreen()
        UiState.Loading -> LoadingScreen()
        is UiState.Error -> ErrorScreen(data)
        is UiState.Data -> DataScreen(data, viewModel)
    }
}

@Composable
fun InitWithPermissions(viewModel: BookSummaryViewModel) {
    // TODO: Check if it works correctly
    val onPermissionGranted = remember(viewModel) { { viewModel.handleIntent(UiIntent.FetchBookSummary) } }
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
            RequestPermission(
                android.Manifest.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK,
                onPermissionGranted
            )
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> {
            RequestPermission(
                android.Manifest.permission.FOREGROUND_SERVICE,
                onPermissionGranted
            )
        }
        else -> onPermissionGranted()
    }
}

@Composable
private fun IdleScreen() {
    // TODO: Handle idle state
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
    val context = LocalContext.current.applicationContext
    LaunchedEffect(viewModel) {
        viewModel.handleIntent(UiIntent.InitPlayer(context))
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        DataLandscapeScreen(data, viewModel)
    } else {
        DataPortraitScreen(data, viewModel)
    }
}

@Composable
fun DataPortraitScreen(data: UiState.Data, viewModel: BookSummaryViewModel) {
    val onToggleAudioSpeed = remember(viewModel) { { viewModel.handleIntent(UiIntent.ToggleAudioSpeed) } }
    val onToggleAudio = remember(viewModel) { { viewModel.handleIntent(UiIntent.ToggleAudio) } }
    val onPlaybackTimeChangeStarted = remember(viewModel) { { viewModel.handleIntent(UiIntent.StartPlaybackPositionChange) } }
    val onPlaybackTimeChangeFinished = remember(viewModel) { { position: Float -> viewModel.handleIntent(UiIntent.FinishPlaybackPositionChange(position.toLong())) } }
    val onRewind = remember(viewModel) { { viewModel.handleIntent(UiIntent.ShiftAudioPosition(REWIND_OFFSET_MILLIS.unaryMinus())) } }
    val onFastForward = remember(viewModel) { { viewModel.handleIntent(UiIntent.ShiftAudioPosition(FAST_FORWARD_OFFSET_MILLIS)) } }
    val onSkipBackward = remember(viewModel) { { viewModel.handleIntent(UiIntent.GoPreviousPart) } }
    val onSkipForward = remember(viewModel) { { viewModel.handleIntent(UiIntent.GoNextPart) } }
    val onAudioModeClick = remember(viewModel) { { viewModel.handleIntent(UiIntent.ToggleSummaryMode) } }
    val onTextModeClick = remember(viewModel) { { viewModel.handleIntent(UiIntent.ToggleSummaryMode) } }

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
fun DataLandscapeScreen(data: UiState.Data, viewModel: BookSummaryViewModel) {
    val onToggleAudioSpeed = remember(viewModel) { { viewModel.handleIntent(UiIntent.ToggleAudioSpeed) } }
    val onToggleAudio = remember(viewModel) { { viewModel.handleIntent(UiIntent.ToggleAudio) } }
    val onPlaybackTimeChangeStarted = remember(viewModel) { { viewModel.handleIntent(UiIntent.StartPlaybackPositionChange) } }
    val onPlaybackTimeChangeFinished = remember(viewModel) { { position: Float -> viewModel.handleIntent(UiIntent.FinishPlaybackPositionChange(position.toLong())) } }
    val onRewind = remember(viewModel) { { viewModel.handleIntent(UiIntent.ShiftAudioPosition(REWIND_OFFSET_MILLIS.unaryMinus())) } }
    val onFastForward = remember(viewModel) { { viewModel.handleIntent(UiIntent.ShiftAudioPosition(FAST_FORWARD_OFFSET_MILLIS)) } }
    val onSkipBackward = remember(viewModel) { { viewModel.handleIntent(UiIntent.GoPreviousPart) } }
    val onSkipForward = remember(viewModel) { { viewModel.handleIntent(UiIntent.GoNextPart) } }
    val onAudioModeClick = remember(viewModel) { { viewModel.handleIntent(UiIntent.ToggleSummaryMode) } }
    val onTextModeClick = remember(viewModel) { { viewModel.handleIntent(UiIntent.ToggleSummaryMode) } }

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
            Spacer(modifier = Modifier.height(32.dp))

            PlaybackControls(
                isAudioPlaying = data.isAudioPlaying,
                onToggleAudio = onToggleAudio,
                onRewind = onRewind,
                onFastForward = onFastForward,
                onSkipBackward = onSkipBackward,
                onSkipForward = onSkipForward,
            )
            Spacer(modifier = Modifier.height(24.dp))

            SummaryModeToggle(
                isListeningModeEnabled = data.isListeningModeEnabled,
                onAudioModeClick = onAudioModeClick,
                onTextModeClick = onTextModeClick,
            )
        }
    }
}

@Composable
private fun TopBookCover(bookCoverUrl: String, modifier: Modifier = Modifier) {
    // TODO: Add placeholder image for: loading, error
    Box(
        modifier = modifier
            .height(350.dp)
            .width(250.dp)
            .padding(top = 32.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = bookCoverUrl),
            contentDescription = "Book Cover",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun PartNumberTitle(partNumber: Int, partsTotal: Int) {
    Text(
        text = stringResource(R.string.key_point_title, partNumber, partsTotal),
        style = MaterialTheme.typography.body1.copy(
            color = MaterialTheme.colors.onBackground,
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
            color = MaterialTheme.colors.secondary
        ),
        textAlign = TextAlign.Center
    )
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
