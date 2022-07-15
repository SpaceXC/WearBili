package cn.spacexc.wearbili.dataclass.dynamic.dynamicforwardshare.card

data class Vip(
    val avatar_subscript: Int,
    val avatar_subscript_url: String,
    val label: Label,
    val nickname_color: String,
    val role: Int,
    val themeType: Int,
    val vipDueDate: Long,
    val vipStatus: Int,
    val vipType: Int
)