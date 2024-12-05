package ua.headway.booksummary.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BookSummaryResponse(
    val id: Long = 0L,
    @Json(name = "summaryParts")
    val summaryParts: List<SummaryPartResponse> = emptyList(),
    @Json(name = "bookCoverUrl")
    val bookCoverUrl: String = ""
) {
    @JsonClass(generateAdapter = true)
    data class SummaryPartResponse(
        val description: String = "",
        val audioUrl: String = "",
        val text: String = ""
    )
}