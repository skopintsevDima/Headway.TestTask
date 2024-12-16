@file:Suppress("DEPRECATION")

package ua.headway.booksummary.presentation.ui.screen.booksummary.composable

import android.content.res.Configuration
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
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.placeholder.placeholder
import ua.headway.booksummary.presentation.ui.resources.LocalResources

@Composable
fun LoadingScreen() {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        LoadingScreenLandscape()
    } else {
        LoadingScreenPortrait()
    }
}

@Composable
private fun LoadingScreenPortrait() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(LocalResources.Dimensions.Padding.Medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(top = LocalResources.Dimensions.Padding.XXXLarge)
                .height(LocalResources.Dimensions.Image.Height)
                .width(LocalResources.Dimensions.Image.Width)
                .placeholder(
                    visible = true,
                    highlight = PlaceholderHighlight.shimmer(),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
                )
        )
        Spacer(modifier = Modifier.height(LocalResources.Dimensions.Padding.XLarge))

        Box(
            modifier = Modifier
                .fillMaxWidth(LocalResources.Dimensions.Size.FillWidth.Half)
                .height(LocalResources.Dimensions.Text.HeightSmall)
                .placeholder(
                    visible = true,
                    highlight = PlaceholderHighlight.shimmer(),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
                )
        )
        Spacer(modifier = Modifier.height(LocalResources.Dimensions.Padding.Small))

        Box(
            modifier = Modifier
                .fillMaxWidth(LocalResources.Dimensions.Size.FillWidth.Large)
                .height(LocalResources.Dimensions.Text.HeightMedium)
                .placeholder(
                    visible = true,
                    highlight = PlaceholderHighlight.shimmer(),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
                )
        )
        Spacer(modifier = Modifier.height(LocalResources.Dimensions.Padding.Medium))

        PlaybackShimmer()
    }
}

@Composable
private fun LoadingScreenLandscape() {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(LocalResources.Dimensions.Padding.Medium)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(horizontal = LocalResources.Dimensions.Padding.Medium)
                .height(LocalResources.Dimensions.Image.Height)
                .placeholder(
                    visible = true,
                    highlight = PlaceholderHighlight.shimmer(),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
                )
        )

        Column(
            modifier = Modifier
                .weight(2f)
                .fillMaxHeight()
                .padding(start = LocalResources.Dimensions.Padding.Medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(LocalResources.Dimensions.Size.FillWidth.Half)
                    .height(LocalResources.Dimensions.Text.HeightSmall)
                    .placeholder(
                        visible = true,
                        highlight = PlaceholderHighlight.shimmer(),
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
                    )
            )
            Spacer(modifier = Modifier.height(LocalResources.Dimensions.Padding.Small))

            Box(
                modifier = Modifier
                    .fillMaxWidth(LocalResources.Dimensions.Size.FillWidth.Large)
                    .height(LocalResources.Dimensions.Text.HeightMedium)
                    .placeholder(
                        visible = true,
                        highlight = PlaceholderHighlight.shimmer(),
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
                    )
            )
            Spacer(modifier = Modifier.height(LocalResources.Dimensions.Padding.Medium))

            PlaybackShimmer()
        }
    }
}

@Composable
private fun PlaybackShimmer() {
    Spacer(modifier = Modifier.height(LocalResources.Dimensions.Padding.Medium))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(LocalResources.Dimensions.Icon.Small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(LocalResources.Dimensions.Icon.Small, LocalResources.Dimensions.Text.HeightSmall)
                .placeholder(
                    visible = true,
                    highlight = PlaceholderHighlight.shimmer(),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
                )
        )
        Spacer(modifier = Modifier.width(LocalResources.Dimensions.Padding.Small))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(LocalResources.Dimensions.Text.SizeTiny)
                .placeholder(
                    visible = true,
                    highlight = PlaceholderHighlight.shimmer(),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
                )
        )
        Spacer(modifier = Modifier.width(LocalResources.Dimensions.Padding.Small))
        Box(
            modifier = Modifier
                .size(LocalResources.Dimensions.Icon.Small, LocalResources.Dimensions.Text.HeightSmall)
                .placeholder(
                    visible = true,
                    highlight = PlaceholderHighlight.shimmer(),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
                )
        )
    }
    Spacer(modifier = Modifier.height(LocalResources.Dimensions.Padding.Medium))

    Box(
        modifier = Modifier
            .width(LocalResources.Dimensions.Button.WidthSmall)
            .height(LocalResources.Dimensions.Button.HeightSmall)
            .placeholder(
                visible = true,
                highlight = PlaceholderHighlight.shimmer(),
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
            )
    )
    Spacer(modifier = Modifier.height(LocalResources.Dimensions.Padding.XLarge))

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(5) {
            Box(
                modifier = Modifier
                    .size(LocalResources.Dimensions.Icon.Medium)
                    .placeholder(
                        visible = true,
                        highlight = PlaceholderHighlight.shimmer(),
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
                    )
            )
        }
    }
    Spacer(modifier = Modifier.height(LocalResources.Dimensions.Padding.Large))
}