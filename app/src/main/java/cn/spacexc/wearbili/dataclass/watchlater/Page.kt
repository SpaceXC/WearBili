package cn.spacexc.wearbili.dataclass.watchlater

data class Page(
    val cid: Long,
    val dimension: Dimension,
    val duration: Int,
    val from: String,
    val page: Int,
    val part: String,
    val vid: String,
    val weblink: String
)