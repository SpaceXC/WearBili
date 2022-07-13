package cn.spacexc.wearbili.dataclass.watchlater

data class WatchLater(
    val code: Int,
    val `data`: Data,
    val message: String,
    val ttl: Int
)