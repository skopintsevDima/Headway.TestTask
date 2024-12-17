package ua.headway.booksummary.presentation.ui.screen.booksummary

import android.content.res.Configuration
import android.os.Build
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import ua.headway.booksummary.presentation.ui.composable.RequestPermission
import ua.headway.booksummary.presentation.ui.screen.booksummary.TestTags.TAG_DATA_SCREEN
import ua.headway.booksummary.presentation.ui.screen.booksummary.TestTags.TAG_DATA_SCREEN_PLACEHOLDER
import ua.headway.booksummary.presentation.ui.screen.booksummary.TestTags.TAG_RETRY_BUTTON
import ua.headway.booksummary.presentation.ui.screen.booksummary.composable.DataListeningScreen
import ua.headway.booksummary.presentation.ui.screen.booksummary.composable.DataReadingScreen
import ua.headway.booksummary.presentation.ui.screen.booksummary.composable.ErrorSnackBar
import ua.headway.booksummary.presentation.ui.screen.booksummary.composable.LoadingScreen
import ua.headway.booksummary.presentation.ui.screen.booksummary.composable.MessageScreen
import ua.headway.booksummary.presentation.ui.screen.booksummary.composable.TopBookCover
import ua.headway.booksummary.presentation.ui.screen.booksummary.mock.DataStatePreviewProvider
import ua.headway.booksummary.presentation.ui.screen.booksummary.mock.ErrorStatePreviewProvider
import ua.headway.booksummary.presentation.ui.screen.booksummary.mock.MockBookSummaryViewModelWithState
import ua.headway.booksummary.presentation.ui.screen.booksummary.mock.MockUiState
import ua.headway.booksummary.presentation.util.Constants.UI.BookSummary
import ua.headway.core.presentation.ui.resources.LocalResources
import ua.headway.core.presentation.ui.theme.HeadwayTestTaskTheme
import ua.headway.core.presentation.ui.util.PreviewLightDarkBothOrientations

@Composable
fun BookSummaryScreen(viewModel: BookSummaryViewModel = hiltViewModel<BookSummaryViewModelImpl>()) {
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
                is UiState.Data -> {
                    if (stateValue.partsTotal > 0) {
                        DataScreen(stateValue, viewModel, snackbarHostState)
                    } else {
                        ErrorScreen(
                            UiState.Error.LoadBookDataError(
                                stringResource(LocalResources.Strings.ErrorLoadBookData)
                            ),
                            viewModel,
                            snackbarHostState
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InitWithPermissions(viewModel: BookSummaryViewModel) {
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
    MessageScreen(message = stringResource(LocalResources.Strings.IdleMessage))
}

@Composable
private fun ErrorScreen(
    error: UiState.Error,
    viewModel: BookSummaryViewModel,
    snackbarHostState: SnackbarHostState
) {
    val errorMessage = rememberSaveable(error) { error.errorMsg }
    val actionLabel = stringResource(LocalResources.Strings.Okay)

    LaunchedEffect(errorMessage) {
        snackbarHostState.showSnackBarSafe(
            message = errorMessage,
            actionLabel = actionLabel
        )
    }

    when (error) {
        is UiState.Error.LoadBookDataError,
        is UiState.Error.NoDataForPlayerError -> {
            val onRetryClick = remember(viewModel) { { viewModel.tryHandleIntent(UiIntent.FetchBookSummary) } }
            DataScreenPlaceholder(onRetryClick)
        }

        is UiState.Error.PlaybackError,
        is UiState.Error.PlayerInitError,
        is UiState.Error.UnknownError -> {
            MessageScreen(message = stringResource(LocalResources.Strings.UnknownErrorMessage))
        }
    }
}

@Composable
private fun DataScreenPlaceholder(onRetryClick: () -> Unit) {
    Box(
        modifier = Modifier
            .testTag(TAG_DATA_SCREEN_PLACEHOLDER)
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
                modifier = Modifier
                    .testTag(TAG_RETRY_BUTTON)
                    .size(LocalResources.Dimensions.Icon.ExtraLarge)
            ) {
                Icon(
                    modifier = Modifier.size(LocalResources.Dimensions.Icon.ExtraLarge),
                    imageVector = ImageVector.vectorResource(LocalResources.Icons.Refresh),
                    contentDescription = stringResource(id = LocalResources.Strings.Retry),
                    tint = MaterialTheme.colors.secondary
                )
            }

            Spacer(modifier = Modifier.height(LocalResources.Dimensions.Padding.Small))

            Text(
                text = stringResource(id = LocalResources.Strings.Retry),
                style = MaterialTheme.typography.body2.copy(
                    color = MaterialTheme.colors.onBackground,
                    fontSize = LocalResources.Dimensions.Text.SizeLarge
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
    val actionLabel = stringResource(LocalResources.Strings.Okay)

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
private fun DataPortraitScreen(data: UiState.Data, viewModel: BookSummaryViewModel) {
    Column(
        modifier = Modifier
            .testTag(TAG_DATA_SCREEN)
            .fillMaxSize()
            .background(color = MaterialTheme.colors.background)
            .padding(WindowInsets.systemBars.asPaddingValues()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBookCover(
            data.bookCoverUrl,
            modifier = Modifier.padding(top = LocalResources.Dimensions.Padding.XXXLarge)
        )
        Spacer(modifier = Modifier.height(LocalResources.Dimensions.Padding.XXLarge))

        if (data.isListeningModeEnabled) {
            DataListeningScreen(
                data = data,
                viewModel = viewModel,
                modifier = Modifier.fillMaxSize(),
                modeTogglePadding = LocalResources.Dimensions.Padding.SummaryToggleBottomPortrait,
                playbackIconSize = LocalResources.Dimensions.Icon.Medium
            )
        } else {
            DataReadingScreen(
                data = data,
                viewModel = viewModel,
                modifier = Modifier.fillMaxSize(),
                modeTogglePadding = LocalResources.Dimensions.Padding.SummaryToggleBottomPortrait
            )
        }
    }
}

@Composable
private fun DataLandscapeScreen(data: UiState.Data, viewModel: BookSummaryViewModel) {
    Row(
        modifier = Modifier
            .testTag(TAG_DATA_SCREEN)
            .fillMaxSize()
            .background(color = MaterialTheme.colors.background)
            .padding(WindowInsets.systemBars.asPaddingValues())
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .padding(horizontal = LocalResources.Dimensions.Padding.Medium)
        ) {
            TopBookCover(bookCoverUrl = data.bookCoverUrl)
        }

        if (data.isListeningModeEnabled) {
            val modifier = Modifier
                .weight(2f)
                .fillMaxHeight()
                .padding(horizontal = LocalResources.Dimensions.Padding.Medium)
            DataListeningScreen(
                data = data,
                viewModel = viewModel,
                modifier = modifier,
                modeTogglePadding = LocalResources.Dimensions.Padding.SummaryToggleBottomLandscape,
                playbackIconSize = LocalResources.Dimensions.Icon.Large
            )
        } else {
            val modifier = Modifier
                .weight(2f)
                .fillMaxHeight()
                .padding(horizontal = LocalResources.Dimensions.Padding.Medium)
            DataReadingScreen(
                data = data,
                viewModel = viewModel,
                modifier = modifier,
                modeTogglePadding = LocalResources.Dimensions.Padding.SummaryToggleBottomLandscape
            )
        }
    }
}

suspend fun SnackbarHostState.showSnackBarSafe(
    message: String,
    actionLabel: String
) {
    try {
        this.showSnackbar(message, actionLabel)
    } catch (e: Throwable) {
        Log.e(BookSummary.TAG, "Failed to show SnackBar: ${e.stackTraceToString()}")
    }
}

@PreviewLightDarkBothOrientations
@Composable
private fun IdlePreview() {
    BookSummaryScreenPreviewWrapper(MockUiState.Idle)
}

@PreviewLightDarkBothOrientations
@Composable
private fun LoadingPreview() {
    BookSummaryScreenPreviewWrapper(MockUiState.Loading)
}

@PreviewLightDarkBothOrientations
@Composable
private fun ErrorPreview(
    @PreviewParameter(ErrorStatePreviewProvider::class) error: UiState.Error
) {
    BookSummaryScreenPreviewWrapper(error)
}

@PreviewLightDarkBothOrientations
@Composable
private fun DataPreview(
    @PreviewParameter(DataStatePreviewProvider::class) data: UiState.Data
) {
    BookSummaryScreenPreviewWrapper(data)
}

@Composable
private fun BookSummaryScreenPreviewWrapper(
    uiState: UiState
) {
    HeadwayTestTaskTheme {
        BookSummaryScreen(
            viewModel = MockBookSummaryViewModelWithState(uiState)
        )
    }
}

object TestTags {
    const val TAG_MESSAGE_SCREEN = "MessageScreen"
    const val TAG_LOADING_SCREEN = "LoadingScreen"
    const val TAG_DATA_SCREEN_PLACEHOLDER = "DataScreenPlaceholder"
    const val TAG_DATA_SCREEN = "DataScreen"
    const val TAG_RETRY_BUTTON = "RetryButton"
}