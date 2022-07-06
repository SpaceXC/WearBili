package cn.spacexc.wearbili.dataclass

data class User(
    val code: Int,
    val `data`: UserData,
    val message: String,
    val ttl: Int
)