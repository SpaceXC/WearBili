package cn.spacexc.wearbili.dataclass.dynamic.dynamictext

data class Desc(
    val acl: Int,
    val comment: Int,
    val dynamic_id: Long,
    val dynamic_id_str: String,
    val inner_id: Int,
    val is_liked: Int,
    val like: Int,
    val orig_dy_id: Long,
    val orig_dy_id_str: String,
    val orig_type: Int,
    val pre_dy_id: Int,
    val pre_dy_id_str: String,
    val r_type: Int,
    val repost: Int,
    val rid: Long,
    val rid_str: String,
    val status: Int,
    val stype: Int,
    val timestamp: Int,
    val type: Int,
    val uid: Long,
    val uid_type: Int,
    val user_profile: UserProfile,
    val view: Int
)