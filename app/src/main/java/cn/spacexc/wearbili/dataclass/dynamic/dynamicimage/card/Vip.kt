package cn.spacexc.wearbili.dataclass.dynamic.dynamicimage.card

data class Vip(
    val avatar_subscript: Int,
    val due_date: Long,
    val label: Label,
    val nickname_color: String,
    val status: Int,
    val theme_type: Int,
    val type: Int,
    val vip_pay_type: Int
)