package cn.spacexc.wearbili.dataclass.dynamic.dynamicepisode

data class EpisodeCard(
    val aid: Long,
    val apiSeasonInfo: ApiSeasonInfo,
    val bullet_count: Int,
    val cover: String,
    val episode_id: Long,
    val index: String,
    val index_title: String,
    val new_desc: String,
    val online_finish: Int,
    val play_count: Int,
    val reply_count: Int,
    val url: String
)