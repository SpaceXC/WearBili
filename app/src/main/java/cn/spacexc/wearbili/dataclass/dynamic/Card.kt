package cn.spacexc.wearbili.dataclass.dynamic

import cn.spacexc.wearbili.dataclass.dynamic.dynamicforwardshare.Origin
import cn.spacexc.wearbili.dataclass.dynamic.dynamicforwardshare.UserProfile

data class Card(
    val card: String,
    var cardObj: Any?,
    val desc: Desc,
    val display: Display?,
    val extend_json: String,
    val extra: Extra?,
    val acl: Int,
    val comment: Int,
    val dynamic_id: Long,
    val dynamic_id_str: String,
    val inner_id: Long,
    val is_liked: Int,
    val like: Int,
    val orig_dy_id: Long,
    val orig_dy_id_str: String,
    val orig_type: Int,
    val origin: Origin,
    val pre_dy_id: Long,
    val pre_dy_id_str: String,
    val r_type: Int,
    val repost: Int,
    val rid: Long,
    val rid_str: String,
    val status: Int,
    val stype: Int,
    val timestamp: Long,
    val type: Int,
    val uid: Long,
    val uid_type: Int,
    val user_profile: UserProfile,
    val view: Int
)