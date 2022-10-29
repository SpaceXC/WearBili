package cn.spacexc.wearbili.dataclass.bangumi

data class PayType(
    val allow_discount: Int,
    val allow_pack: Int,
    val allow_ticket: Int,
    val allow_time_limit: Int,
    val allow_vip_discount: Int,
    val forbid_bb: Int
)