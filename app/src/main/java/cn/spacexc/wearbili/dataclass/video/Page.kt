package cn.spacexc.wearbili.dataclass.video

data class Page(
    val cid: Long,
    val dimension: Dimension,
    val duration: Int,
    val first_frame: String,
    val from: String,
    val page: Int,
    val part: String,
    val vid: String,
    val weblink: String
)