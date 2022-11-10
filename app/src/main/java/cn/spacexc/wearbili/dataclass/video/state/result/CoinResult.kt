package cn.spacexc.wearbili.dataclass.video.state.result

data class CoinResult(
    val code: Int,
    val `data`: Data,
    val message: String,
    val ttl: Int
) {
    data class Data(
        val like: Boolean
    )
}