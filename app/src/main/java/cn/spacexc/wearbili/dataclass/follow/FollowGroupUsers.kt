package cn.spacexc.wearbili.dataclass.follow

data class FollowGroupUsers(
    val code: Int,
    val `data`: List<FollowGroupUserItem>,
    val message: String,
    val ttl: Int
)