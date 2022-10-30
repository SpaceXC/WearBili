package cn.spacexc.wearbili.dataclass.bangumi

data class UpInfo(
    val avatar: String,
    val avatar_subscript_url: String,
    val follower: Int,
    val is_follow: Int,
    val mid: Long,
    val nickname_color: String,
    val pendant: Pendant,
    val theme_type: Int,
    val uname: String,
    val verify_type: Int,
    val vip_label: VipLabel,
    val vip_status: Int,
    val vip_type: Int
)