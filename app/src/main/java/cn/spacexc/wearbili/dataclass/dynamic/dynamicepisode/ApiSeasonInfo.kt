package cn.spacexc.wearbili.dataclass.dynamic.dynamicepisode

data class ApiSeasonInfo(
    val bgm_type: Int,
    val cover: String,
    val is_finish: Int,
    val season_id: Long,
    val title: String,
    val total_count: Int,
    val ts: Int,
    val type_name: String
)