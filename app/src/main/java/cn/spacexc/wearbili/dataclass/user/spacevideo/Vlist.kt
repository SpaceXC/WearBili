package cn.spacexc.wearbili.dataclass.user.spacevideo

data class Vlist(
    val aid: Long,
    val attribute: Int,
    val author: String,
    val bvid: String,
    val comment: Int,
    val copyright: String,
    val created: Int,
    val description: String,
    val hide_click: Boolean,
    val is_avoided: Int,
    val is_live_playback: Int,
    val is_pay: Int,
    val is_steins_gate: Int,
    val is_union_video: Int,
    val length: String,
    val meta: Meta?,
    val mid: Long,
    val pic: String,
    val play: Int,
    val review: Int,
    val subtitle: String,
    val title: String,
    val typeid: Long,
    val video_review: Int
)