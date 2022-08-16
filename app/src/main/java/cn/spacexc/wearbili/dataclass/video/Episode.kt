package cn.spacexc.wearbili.dataclass.video

data class Episode(
    val aid: Int,
    val arc: Arc,
    val attribute: Int,
    val bvid: String,
    val cid: Int,
    val id: Int,
    val page: PageX,
    val season_id: Int,
    val section_id: Int,
    val title: String
)