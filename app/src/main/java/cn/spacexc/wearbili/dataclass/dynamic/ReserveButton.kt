package cn.spacexc.wearbili.dataclass.dynamic

data class ReserveButton(
    val check: Check,
    val status: Int,
    val type: Int,
    val uncheck: Uncheck
)