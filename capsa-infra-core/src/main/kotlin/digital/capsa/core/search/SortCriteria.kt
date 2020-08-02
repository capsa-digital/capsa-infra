package digital.capsa.core.search

class SortCriteria<T> (
    val firstSortBy: T?,
    val firstSortDescending: Boolean?,
    val secondSortBy: T?,
    val secondSortDescending: Boolean?
)