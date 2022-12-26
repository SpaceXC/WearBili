package cn.spacexc.wearbili.dataclass.video

data class Section(
    val episodes: List<Episode>,
    val id: Long,
    val season_id: Long,
    val title: String,
    val type: Int
)