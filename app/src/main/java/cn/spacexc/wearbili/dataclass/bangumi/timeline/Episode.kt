package cn.spacexc.wearbili.dataclass.bangumi.timeline

data class Episode(
    val cover: String,
    val delay: Int,
    val delay_id: Long,
    val delay_index: String,
    val delay_reason: String,
    val ep_cover: String,
    val episode_id: Long,
    val follows: String,
    val plays: String,
    val pub_index: String,
    val pub_time: String,
    val pub_ts: Long,
    val published: Int,
    val season_id: Long,
    val square_cover: String,
    val title: String
)