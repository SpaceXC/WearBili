package cn.spacexc.wearbili.dataclass.user

data class UserPendant(
    val expire: Int,
    val image: String,
    val image_enhance: String,
    val image_enhance_frame: String,
    val name: String,
    val pid: Int
)