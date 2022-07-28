package cn.spacexc.wearbili.dataclass.dynamic

data class Origin(
    val acl: Int,
    val bvid: String?,
    val dynamic_id: Long,
    val dynamic_id_str: String,
    val inner_id: Int?,
    val orig_dy_id: Int?,
    val orig_dy_id_str: String,
    val pre_dy_id: Int?,
    val pre_dy_id_str: String,
    val r_type: Int?,
    val repost: Int,
    val rid: Long,
    val rid_str: String,
    val status: Int,
    val stype: Int?,
    val timestamp: Int,
    val type: Int,
    val uid: Int,
    val uid_type: Int,
    val view: Int
)