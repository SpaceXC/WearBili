package cn.spacexc.wearbili.dataclass.video

data class Episode(
    val aid: Long,
    val arc: Arc,
    val attribute: Int,
    val bvid: String,
    val cid: Long,
    val id: Long,
    val page: PageX,
    val season_id: Long,
    val section_id: Long,
    val title: String
)