package digital.capsa.archetypes.query.controller.dto

import digital.capsa.archetypes.core.vocab.BookSortByType
import digital.capsa.archetypes.core.vocab.BookStatus
import java.util.UUID

interface BookDTO {
    val bookId: UUID
    val bookTitle: String
    val authorName: String
    val bookStatus: BookStatus
    val memberId: UUID?
}

data class BookInfoDTO(
    override val bookId: UUID,
    override val bookTitle: String,
    override val authorName: String,
    override val bookStatus: BookStatus,
    override val memberId: UUID? = null
) : BookDTO

data class BookDetailsDTO(
        private val bookInfo: BookInfoDTO,
        val memberFirstName: String?,
        val memberLastName: String?,
        val memberEmail: String?
) : BookDTO by bookInfo

data class BookSearchCriteriaDTO(
    val libraryId: UUID,
    val pageNumber: Int = 1,
    val pageSize: Int = 15,
    val bookStatus: BookStatus?,
    val search: String?,
    val firstSortBy: BookSortByType?,
    val firstSortDescending: Boolean?,
    val secondSortBy: BookSortByType?,
    val secondSortDescending: Boolean?
)

data class BookSearchResultDTO(
        val bookList: List<BookInfoDTO>
)