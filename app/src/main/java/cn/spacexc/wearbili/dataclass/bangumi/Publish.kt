package cn.spacexc.wearbili.dataclass.bangumi

data class Publish(
    val is_finish: Int,
    val is_started: Int,
    val pub_time: String,
    val pub_time_show: String,
    val unknow_pub_date: Int,
    val weekday: Int
)