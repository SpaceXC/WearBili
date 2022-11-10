package cn.spacexc.wearbili.dataclass.video.state.result

data class FavResult(
    val code: Int,
    val `data`: Data,
    val message: String
) {
    data class Data(
        val prompt: Boolean
    )
}