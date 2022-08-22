package cn.spacexc.wearbili.dataclass.dynamic.dynamicvideo

data class Desc(
    val bvid: String,
    val dynamic_id: Long,
    val dynamic_id_str: String,
    val is_liked: Int,
    val like: Int,
    val orig_dy_id_str: String,
    val orig_type: Int,
    val pre_dy_id_str: String,
    val repost: Int,
    val rid: Long,
    val rid_str: String,
    val status: Int,
    val timestamp: Int,
    val type: Int,
    val uid: Long,
    val uid_type: Int,
    val user_profile: UserProfile,
    val view: Int
)