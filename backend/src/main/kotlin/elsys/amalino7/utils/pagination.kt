package elsys.amalino7.utils

import kotlinx.serialization.Serializable

@Serializable
data class PageRequest(val page: Long, val size: Int, val sort: Sort)

@Serializable
data class PageResult<T>(val items: List<T>, val totalCount: Long?)

@Serializable
data class Sort(val field: String, val direction: Direction)

enum class Direction { ASC, DESC, NONE }