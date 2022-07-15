package cn.spacexc.wearbili.dataclass.dynamic

data class UserProfile(
    val card: CardX,
    val decorate_card: DecorateCard?,
    val info: Info,
    val level_info: LevelInfo,
    val pendant: Pendant,
    val rank: String,
    val sign: String,
    val vip: Vip
)