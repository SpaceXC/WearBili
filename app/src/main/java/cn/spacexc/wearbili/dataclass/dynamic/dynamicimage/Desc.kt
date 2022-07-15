package cn.spacexc.wearbili.dataclass.dynamic.dynamicimage

data class Desc(
    val comment: Int,
    val dynamic_id: Long,
    val dynamic_id_str: String,
    val is_liked: Int,
    val like: Int,
    val orig_dy_id_str: String,
    val orig_type: Int,
    val pre_dy_id_str: String,
    val repost: Int,
    val rid: Int,
    val rid_str: String,
    val status: Int,
    val timestamp: Int,
    val type: Int,
    val uid: Int,
    val uid_type: Int,
    val user_profile: UserProfile,
    val view: Int
)