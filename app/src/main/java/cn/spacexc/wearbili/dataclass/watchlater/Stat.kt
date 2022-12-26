package cn.spacexc.wearbili.dataclass.watchlater

data class Stat(
    val aid: Long,
    val coin: Int,
    val danmaku: Int,
    val dislike: Int,
    val favorite: Int,
    val his_rank: Int,
    val like: Int,
    val now_rank: Int,
    val reply: Int,
    val share: Int,
    val view: Int
)