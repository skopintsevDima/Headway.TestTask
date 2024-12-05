package ua.headway.booksummary.presentation.ui.screen.booksummary

import android.os.Build
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import ua.headway.booksummary.R
import ua.headway.booksummary.presentation.ui.composable.RequestPermission
import ua.headway.booksummary.presentation.ui.resources.Constants.UI.BookSummary
import ua.headway.booksummary.presentation.ui.resources.LocalResources
import ua.headway.booksummary.presentation.ui.screen.booksummary.composable.DataListeningScreen
import ua.headway.booksummary.presentation.ui.screen.booksummary.composable.DataReadingScreen
import ua.headway.booksummary.presentation.ui.screen.booksummary.composable.ErrorSnackBar
import ua.headway.booksummary.presentation.ui.screen.booksummary.composable.LoadingScreen
import ua.headway.booksummary.presentation.ui.screen.booksummary.composable.MessageScreen

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
    MessageScreen(message = stringResource(R.string.idle_message))
}

@Composable
private fun ErrorScreen(
    error: UiState.Error,
    viewModel: BookSummaryViewModel,
    snackbarHostState: SnackbarHostState
) {
    val errorMessage = rememberSaveable(error) { error.errorMsg }
    val actionLabel = stringResource(R.string.okay)

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
            MessageScreen(message = stringResource(R.string.unknown_error_message_to_user))
        }
    }
}

@Composable
private fun DataScreenPlaceholder(onRetryClick: () -> Unit) {
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

    LaunchedEffect(data.nonCriticalError) {
        if (!nonCriticalErrorMsg.isNullOrEmpty()) {
            snackbarHostState.showSnackBarSafe(
                message = nonCriticalErrorMsg,
                actionLabel = actionLabel
            )
        }
    }

    if (data.isListeningModeEnabled) {
        DataListeningScreen(data, viewModel)
    } else {
        val onSkipBack = remember(viewModel) { { viewModel.tryHandleIntent(UiIntent.GoPreviousPart) } }
        val onSkipForward = remember(viewModel) { { viewModel.tryHandleIntent(UiIntent.GoNextPart) } }

        DataReadingScreen(
            data = data,
            viewModel = viewModel,
            onSkipBack = onSkipBack,
            onSkipForward = onSkipForward
        )
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