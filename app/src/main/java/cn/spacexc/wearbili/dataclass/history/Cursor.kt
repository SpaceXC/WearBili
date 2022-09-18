package cn.spacexc.wearbili.dataclass.history

data class Cursor(
    val business: String,
    val max: Int,
    val ps: Int,
    val view_at: Int
)