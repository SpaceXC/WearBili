package cn.spacexc.wearbili.dataclass.dynamic

data class ReserveAttachCard(
    val desc_first: DescFirst,
    val desc_second: String,
    val livePlanStartTime: Int,
    val oid_str: String,
    val origin_state: Int,
    val reserve_button: ReserveButton,
    val reserve_lottery: ReserveLottery,
    val reserve_total: Int,
    val show_desc_second: Boolean,
    val state: Int,
    val stype: Int,
    val title: String,
    val type: String,
    val up_mid: Int,
    val user_info: UserInfo
)