package cn.spacexc.wearbili.dataclass.videoDetail

data class Elector(
    val avatar: String,
    val message: String,
    val mid: Int,
    val pay_mid: Int,
    val rank: Int,
    val trend_type: Int,
    val uname: String,
    val vip_info: VipInfo
)