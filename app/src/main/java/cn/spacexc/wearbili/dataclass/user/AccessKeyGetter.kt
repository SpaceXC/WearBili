package cn.spacexc.wearbili.dataclass.user

data class AccessKeyGetter(
    val code: Int,
    val `data`: Data,
    val status: Boolean,
    val ts: Int
)