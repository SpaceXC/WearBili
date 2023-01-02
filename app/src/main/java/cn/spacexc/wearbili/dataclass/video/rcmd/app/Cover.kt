package cn.spacexc.wearbili.dataclass.video.rcmd.app

data class Cover(
    val gif_tag_show: Boolean,
    val gif_url: String,
    val image_height: Int,
    val image_width: Int,
    val loop: Int,
    val url: String
)