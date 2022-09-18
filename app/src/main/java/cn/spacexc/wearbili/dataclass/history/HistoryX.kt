package cn.spacexc.wearbili.dataclass.history

data class HistoryX(
    val business: String,
    val bvid: String?,
    val cid: Int,
    val dt: Int,
    val epid: Int,
    val oid: Int,
    val page: Int,
    val part: String
)