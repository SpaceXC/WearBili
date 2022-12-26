package cn.spacexc.wearbili.dataclass.history

data class HistoryX(
    val business: String,
    val bvid: String?,
    val cid: Long,
    val dt: Int,
    val epid: Long,
    val oid: Long,
    val page: Int,
    val part: String
)