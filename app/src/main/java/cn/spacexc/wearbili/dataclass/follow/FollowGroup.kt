package cn.spacexc.wearbili.dataclass.follow

data class FollowGroup(
    val code: Int,
    val `data`: MutableList<Group>,
    val message: String,
    val ttl: Int
)