package cn.spacexc.wearbili.dataclass.video.parts

data class VideoParts(
    val code: Int,
    val `data`: List<VideoPartItem>,
    val message: String,
    val ttl: Int
)