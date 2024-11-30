package ua.headway.booksummary.domain.model

data class BookSummary(
    val summaryParts: List<SummaryPart>,
    val bookCoverUrl: String
) {
    data class SummaryPart(
        val description: String,
        val audioUrl: String,
        val audioDurationMs: Float,
        val text: String
    )
}