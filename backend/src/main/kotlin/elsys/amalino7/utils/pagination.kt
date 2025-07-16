package elsys.amalino7.utils

data class PageRequest(val page: Int, val size: Int, val sort: Sort)
data class PageResult<T>(val items: List<T>, val totalCount: Long)
data class Sort(val field: String, val direction: Direction)

enum class Direction { ASC, DESC }