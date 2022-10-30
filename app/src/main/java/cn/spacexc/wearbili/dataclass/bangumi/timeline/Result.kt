package cn.spacexc.wearbili.dataclass.bangumi.timeline

data class Result(
    val date: String,
    val date_ts: Int,
    val day_of_week: Int,
    val episodes: List<Episode>,
    val is_today: Int
)