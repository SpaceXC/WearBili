package cn.spacexc.wearbili.dataclass.video

data class PageX(
    val cid: Long,
    val dimension: Dimension,
    val duration: Int,
    val from: String,
    val page: Int,
    val part: String,
    val vid: String,
    val weblink: String
)