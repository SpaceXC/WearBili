package cn.spacexc.wearbili.dataclass.videoDetail

data class Elec(
    val count: Int,
    val elec_num: Int,
    val elec_set: ElecSet,
    val list: List<Elector>,
    val show: Boolean,
    val total: Int
)