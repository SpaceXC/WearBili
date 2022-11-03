package cn.spacexc.wearbili.dataclass.videoDetail

data class Page(
    val cid: Long,
    val dimension: Dimension,
    val dm: Dm,
    val dmlink: String,
    val download_subtitle: String,
    val download_title: String,
    val duration: Int,
    val first_frame: String,
    val from: String,
    val metas: List<Meta>,
    val page: Int,
    val part: String,
    val vid: String,
    val weblink: String
)