package ua.headway.booksummary.presentation.ui.screen.booksummary.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import coil.compose.rememberAsyncImagePainter
import ua.headway.booksummary.presentation.ui.resources.LocalResources

@Composable
fun TopBookCover(
    bookCoverUrl: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(LocalResources.Dimensions.Image.Height)
            .width(LocalResources.Dimensions.Image.Width)
    ) {
        val painter = rememberAsyncImagePainter(
            model = bookCoverUrl,
            placeholder = painterResource(id = LocalResources.Images.PlaceholderImage),
            error = painterResource(id = LocalResources.Images.PlaceholderError),
            contentScale = ContentScale.Crop
        )

        Image(
            painter = painter,
            contentDescription = "Book Cover",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun PartNumberTitle(partNumber: Int, partsTotal: Int) {
    Text(
        text = stringResource(LocalResources.Strings.KeyPointTitle, partNumber, partsTotal),
        style = MaterialTheme.typography.body1.copy(
            color = MaterialTheme.colors.onBackground,
            fontWeight = FontWeight.Medium,
            fontSize = LocalResources.Dimensions.Text.SizeSmall,
            letterSpacing = LocalResources.Dimensions.Text.SpacingLarge
        )
    )
}

@Composable
fun PartDescription(currentPartDescription: String) {
    Text(
        modifier = Modifier.padding(horizontal = LocalResources.Dimensions.Padding.Small),
        text = currentPartDescription,
        style = MaterialTheme.typography.body1.copy(
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colors.secondary
        ),
        textAlign = TextAlign.Center
    )
}

@Composable
fun SummaryModeToggle(
    isListeningModeEnabled: Boolean,
    onAudioModeClick: () -> Unit,
    onTextModeClick: () -> Unit
) {
    val rowShape = RoundedCornerShape(50)
    Row(
        modifier = Modifier
            .clip(rowShape)
            .background(LocalResources.Colors.White)
            .border(
                width = LocalResources.Dimensions.Size.BorderWidth,
                color = LocalResources.Colors.LightGray,
                shape = rowShape
            ),
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
            .padding(LocalResources.Dimensions.Padding.ExtraSmall)
            .wrapContentWidth()
            .wrapContentHeight()
            .background(
                color = LocalResources.Colors.Blue.takeIf { isSelected }
                    ?: LocalResources.Colors.White,
                shape = RoundedCornerShape(50)
            )
    ) {
        Icon(
            modifier = Modifier.padding(LocalResources.Dimensions.Padding.Medium),
            imageVector = icon,
            contentDescription = description,
            tint = LocalResources.Colors.White.takeIf { isSelected } ?: LocalResources.Colors.Black,
        )
    }
}

@Composable
fun ErrorSnackBar(snackbarHostState: SnackbarHostState) {
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