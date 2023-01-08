package cn.spacexc.wearbili.dataclass.videoDetail

data class OwnerExt(
    val arc_count: String,
    val assists: List<Long>,
    val fans: Int,
    val live: Live,
    val official_verify: OfficialVerify,
    val vip: Vip
)