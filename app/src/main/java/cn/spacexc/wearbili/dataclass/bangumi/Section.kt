package cn.spacexc.wearbili.dataclass.bangumi

data class Section(
    val attr: Int,
    val episode_id: Long,
    val episode_ids: List<Any>,
    val episodes: List<EpisodeX>,
    val id: Long,
    val title: String,
    val type: Int
)