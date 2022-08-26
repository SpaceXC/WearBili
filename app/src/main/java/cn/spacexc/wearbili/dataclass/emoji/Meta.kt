package cn.spacexc.wearbili.dataclass.emoji

data class Meta(
    val alias: String,
    val gif_url: String,
    val size: Int,
    val suggest: List<String>
)