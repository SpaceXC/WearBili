package cn.spacexc.wearbili.dataclass.video

data class Section(
    val episodes: List<Episode>,
    val id: Int,
    val season_id: Int,
    val title: String,
    val type: Int
)