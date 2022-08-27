package cn.spacexc.wearbili.dataclass.videoDetail

data class Vip(
    val accessStatus: Int,
    val dueRemark: String,
    val label: LabelX,
    val themeType: Int,
    val vipDueDate: Long,
    val vipStatus: Int,
    val vipStatusWarn: String,
    val vipType: Int
)