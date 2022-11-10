package cn.spacexc.wearbili.dataclass.video.state

data class CoinState(
    val code: Int,
    val `data`: Data,
    val message: String,
    val ttl: Int
) {
    data class Data(
        val multiply: Int
    )
}