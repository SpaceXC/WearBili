package cn.spacexc.wearbili.dataclass.videoDetail.web

data class Staff(
    val face: String,
    val follower: Int,
    val label_style: Int,
    val mid: Long,
    val name: String,
    val official: Official,
    val title: String,
    val vip: Vip
)