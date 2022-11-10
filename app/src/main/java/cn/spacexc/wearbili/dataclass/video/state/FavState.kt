package cn.spacexc.wearbili.dataclass.video.state

data class FavState(
    val code: Int,
    val `data`: Data,
    val message: String,
    val ttl: Int
) {
    data class Data(
        val count: Int,
        val favoured: Boolean
    )
}