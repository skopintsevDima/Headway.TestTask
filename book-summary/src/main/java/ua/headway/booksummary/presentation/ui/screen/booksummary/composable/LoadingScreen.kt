package ua.headway.booksummary.presentation.ui.screen.booksummary.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.placeholder.placeholder
import ua.headway.booksummary.presentation.ui.resources.LocalResources

@Suppress("DEPRECATION")
@Composable
fun LoadingScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(LocalResources.Dimensions.Padding.Medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .height(LocalResources.Dimensions.Image.Height)
                .width(LocalResources.Dimensions.Image.Width)
                .placeholder(
                    visible = true,
                    highlight = PlaceholderHighlight.shimmer(),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
                )
        )
        Spacer(modifier = Modifier.height(LocalResources.Dimensions.Padding.ExtraLarge))

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
        Spacer(modifier = Modifier.height(LocalResources.Dimensions.Padding.Small))

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
        Spacer(modifier = Modifier.height(LocalResources.Dimensions.Padding.Medium))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(LocalResources.Dimensions.Icon.Small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(LocalResources.Dimensions.Icon.Small, 20.dp)
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
                    .height(10.dp)
                    .placeholder(
                        visible = true,
                        highlight = PlaceholderHighlight.shimmer(),
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
                    )
            )
            Spacer(modifier = Modifier.width(LocalResources.Dimensions.Padding.Small))
            Box(
                modifier = Modifier
                    .size(LocalResources.Dimensions.Icon.Small, 20.dp)
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
                .width(100.dp)
                .height(40.dp)
                .placeholder(
                    visible = true,
                    highlight = PlaceholderHighlight.shimmer(),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
                )
        )
        Spacer(modifier = Modifier.height(LocalResources.Dimensions.Padding.ExtraLarge))

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