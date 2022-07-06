package cn.spacexc.wearbili.dataclass.user

data class UserWatchedShow(
    val icon: String,
    val icon_location: String,
    val icon_web: String,
    val num: Int,
    val switch: Boolean,
    val text_large: String,
    val text_small: String
)