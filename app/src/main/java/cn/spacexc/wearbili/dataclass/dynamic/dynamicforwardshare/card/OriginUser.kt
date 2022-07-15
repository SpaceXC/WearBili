package cn.spacexc.wearbili.dataclass.dynamic.dynamicforwardshare.card

data class OriginUser(
    val card: Card,
    val info: Info,
    val level_info: LevelInfo,
    val pendant: Pendant,
    val rank: String,
    val sign: String,
    val vip: Vip
)