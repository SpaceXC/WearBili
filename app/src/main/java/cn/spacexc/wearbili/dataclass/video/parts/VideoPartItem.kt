package cn.spacexc.wearbili.dataclass.video.parts

data class VideoPartItem(
    val cid: Int,
    val dimension: Dimension,
    val duration: Int,
    val from: String,
    val page: Int,
    val part: String,
    val vid: String,
    val weblink: String
)