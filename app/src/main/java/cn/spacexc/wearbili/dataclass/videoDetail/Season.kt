package cn.spacexc.wearbili.dataclass.videoDetail

data class Season(
    val cover: String,
    val is_finish: String,
    val is_jump: Int,
    val newest_ep_id: String,
    val newest_ep_index: String,
    val ogv_play_url: String,
    val season_id: String,
    val title: String,
    val total_count: String,
    val user_season: UserSeason,
    val weekday: String
)