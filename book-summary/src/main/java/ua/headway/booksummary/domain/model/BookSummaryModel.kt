package ua.headway.booksummary.domain.model

data class BookSummaryModel(
    val id: Long,
    val summaryParts: List<SummaryPart>,
    val bookCoverUrl: String
) {
    data class SummaryPart(
        val description: String,
        val audioUrl: String,
        val text: String
    )
}