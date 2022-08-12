package cn.spacexc.wearbili.dataclass.follow

data class ContractInfo(
    val is_contract: Boolean,
    val is_contractor: Boolean,
    val ts: Int,
    val user_attr: Int
)