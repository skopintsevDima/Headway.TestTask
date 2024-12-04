package ua.headway.booksummary.presentation.ui.screen.booksummary

import android.content.res.Configuration
import android.os.Build
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
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
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.placeholder.placeholder
import ua.headway.booksummary.R
import ua.headway.booksummary.presentation.ui.composable.RequestPermission
import ua.headway.booksummary.presentation.ui.resources.Constants.UI.BookSummary
import ua.headway.booksummary.presentation.ui.resources.Constants.UI.BookSummary.FAST_FORWARD_OFFSET_MILLIS
import ua.headway.booksummary.presentation.ui.resources.Constants.UI.BookSummary.REWIND_OFFSET_MILLIS
import ua.headway.booksummary.presentation.ui.resources.LocalResources
import ua.headway.booksummary.presentation.ui.tool.formatTime

// TODO: (EVERYWHERE) Remove all magic constants into Constants + texts to Strings
// TODO: Remove MaterialTheme import ---> Apply theme from app module
@Composable
fun BookSummaryScreen(viewModel: BookSummaryViewModel = hiltViewModel()) {
    InitWithPermissions(viewModel)

    val uiState = viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(snackbarHost = { ErrorSnackBar(snackbarHostState) }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val stateValue = uiState.value) {
                UiState.Idle -> IdleScreen()
                UiState.Loading -> LoadingScreen()
                is UiState.Error -> ErrorScreen(stateValue, viewModel, snackbarHostState)
                is UiState.Data -> DataScreen(stateValue, viewModel, snackbarHostState)
            }
        }
    }
}

@Composable
fun InitWithPermissions(viewModel: BookSummaryViewModel) {
    // TODO: Check if it works correctly
    val onPermissionGranted = remember(viewModel) { { viewModel.tryHandleIntent(UiIntent.FetchBookSummary) } }
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
    MessageScreen(message = stringResource(R.string.idle_message))
}

@Composable
private fun MessageScreen(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.body1.copy(
                color = MaterialTheme.colors.onBackground,
                fontSize = 24.sp
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LoadingScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .height(350.dp)
                .width(250.dp)
                .placeholder(
                    visible = true,
                    highlight = PlaceholderHighlight.shimmer(),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
                )
        )
        Spacer(modifier = Modifier.height(48.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(20.dp)
                .placeholder(
                    visible = true,
                    highlight = PlaceholderHighlight.shimmer(),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
                )
        )
        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(60.dp)
                .placeholder(
                    visible = true,
                    highlight = PlaceholderHighlight.shimmer(),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
                )
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp, 20.dp)
                    .placeholder(
                        visible = true,
                        highlight = PlaceholderHighlight.shimmer(),
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
                    )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(10.dp)
                    .placeholder(
                        visible = true,
                        highlight = PlaceholderHighlight.shimmer(),
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
                    )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(40.dp, 20.dp)
                    .placeholder(
                        visible = true,
                        highlight = PlaceholderHighlight.shimmer(),
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
                    )
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .width(100.dp)
                .height(40.dp)
                .placeholder(
                    visible = true,
                    highlight = PlaceholderHighlight.shimmer(),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
                )
        )
        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(5) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .placeholder(
                            visible = true,
                            highlight = PlaceholderHighlight.shimmer(),
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
                        )
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(40.dp)
                .placeholder(
                    visible = true,
                    highlight = PlaceholderHighlight.shimmer(),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
                )
        )
    }
}

@Composable
fun ErrorScreen(
    error: UiState.Error,
    viewModel: BookSummaryViewModel,
    snackbarHostState: SnackbarHostState
) {
    val errorMessage = rememberSaveable(error) { error.errorMsg }
    val actionLabel = stringResource(R.string.okay)

    // TODO: Don't show error message again after rotation
    LaunchedEffect(errorMessage) {
        snackbarHostState.showSnackBarSafe(
            message = errorMessage,
            actionLabel = actionLabel
        )
    }

    when (error) {
        is UiState.Error.LoadBookDataError,
        UiState.Error.NoDataForPlayerError -> {
            val onRetryClick = remember(viewModel) { { viewModel.tryHandleIntent(UiIntent.FetchBookSummary) } }
            DataScreenPlaceholder(onRetryClick)
        }

        is UiState.Error.PlaybackError,
        is UiState.Error.PlayerInitError,
        UiState.Error.UnknownError -> {
            MessageScreen(message = stringResource(R.string.unknown_error_message_to_user))
        }
    }
}

@Composable
private fun ErrorSnackBar(snackbarHostState: SnackbarHostState) {
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues()),
        snackbar = { snackbarData ->
            Snackbar(
                action = {
                    snackbarData.actionLabel?.let { actionLabel ->
                        TextButton(onClick = { snackbarData.dismiss() }) {
                            Text(text = actionLabel, color = LocalResources.Colors.LightGray)
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
fun DataScreenPlaceholder(onRetryClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = onRetryClick,
                modifier = Modifier.wrapContentSize()
            ) {
                Icon(
                    modifier = Modifier.size(96.dp),
                    imageVector = ImageVector.vectorResource(LocalResources.Icons.Refresh),
                    contentDescription = stringResource(id = R.string.retry),
                    tint = MaterialTheme.colors.secondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.retry),
                style = MaterialTheme.typography.body2.copy(
                    color = MaterialTheme.colors.onBackground,
                    fontSize = 24.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun DataScreen(
    data: UiState.Data,
    viewModel: BookSummaryViewModel,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current.applicationContext
    LaunchedEffect(viewModel) {
        viewModel.tryHandleIntent(UiIntent.InitPlayer(context))
    }

    val nonCriticalErrorMsg = remember(data.nonCriticalError) { data.nonCriticalError?.errorMsg }
    val actionLabel = stringResource(R.string.okay)

    // TODO: Don't show error message again after rotation
    LaunchedEffect(data.nonCriticalError) {
        if (!nonCriticalErrorMsg.isNullOrEmpty()) {
            snackbarHostState.showSnackBarSafe(
                message = nonCriticalErrorMsg,
                actionLabel = actionLabel
            )
        }
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
fun DataLandscapeScreen(data: UiState.Data, viewModel: BookSummaryViewModel) {
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

private suspend fun SnackbarHostState.showSnackBarSafe(
    message: String,
    actionLabel: String
) {
    try {
        this.showSnackbar(message, actionLabel)
    } catch (e: Throwable) {
        Log.e(BookSummary.TAG, "Failed to show SnackBar: ${e.stackTraceToString()}")
    }
}