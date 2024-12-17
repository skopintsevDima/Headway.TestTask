package ua.headway.booksummary.presentation.ui.screen.booksummary.mapper

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import ua.headway.booksummary.presentation.ui.screen.booksummary.UiState
import ua.headway.core.presentation.ui.resources.LocalResources
import ua.headway.core.presentation.ui.resources.provider.ResourceProvider

interface BookSummaryUiStateMapper {
    fun toMediaItems(data: UiState.Data): List<MediaItem>
}

class DefaultBookSummaryUiStateMapper(
    private val resourceProvider: ResourceProvider
): BookSummaryUiStateMapper {
    override fun toMediaItems(data: UiState.Data): List<MediaItem> {
        return data.summaryParts.mapIndexed { index, summaryPart ->
            val metadata = MediaMetadata.Builder()
                .setTitle(resourceProvider.getString(
                    LocalResources.Strings.KeyPointTitle,
                    index + 1,
                    data.partsTotal
                ))
                .setDescription(summaryPart.description)
                .setArtworkUri(Uri.parse(data.bookCoverUrl))
                .build()

            MediaItem.Builder()
                .setUri(summaryPart.audioUrl)
                .setMediaMetadata(metadata)
                .build()
        }
    }
}