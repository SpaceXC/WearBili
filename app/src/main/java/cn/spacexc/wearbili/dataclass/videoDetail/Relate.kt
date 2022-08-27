package cn.spacexc.wearbili.dataclass.videoDetail

import cn.spacexc.wearbili.dataclass.video.Stat

data class Relate(
    val BadgeStyle: Any,
    val PowerIconStyle: Any,
    val ad_index: Int,
    val aid: Int,
    val card_index: Int,
    val cid: Int,
    val client_ip: String,
    val dimension: Dimension,
    val duration: Int,
    val from_source_id: String,
    val from_source_type: Int,
    val goto: String,
    val is_ad_loc: Boolean,
    val owner: Owner,
    val `param`: String,
    val pic: String,
    val rank_info: Any,
    val rec_three_point: RecThreePoint,
    val request_id: String,
    val src_id: Int,
    val stat: Stat,
    val title: String,
    val trackid: String,
    val uri: String
)