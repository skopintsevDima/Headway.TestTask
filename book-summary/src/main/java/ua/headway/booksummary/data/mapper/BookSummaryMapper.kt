package ua.headway.booksummary.data.mapper

import ua.headway.booksummary.data.api.BookSummaryResponse
import ua.headway.booksummary.domain.model.BookSummaryModel

fun BookSummaryResponse.toDomain(): BookSummaryModel {
    return BookSummaryModel(
        id = this.id,
        bookCoverUrl = this.bookCoverUrl,
        summaryParts = this.summaryParts.map { it.toDomain() }
    )
}

fun BookSummaryResponse.SummaryPartResponse.toDomain(): BookSummaryModel.SummaryPart {
    return BookSummaryModel.SummaryPart(
        description = this.description,
        audioUrl = this.audioUrl,
        text = this.text
    )
}