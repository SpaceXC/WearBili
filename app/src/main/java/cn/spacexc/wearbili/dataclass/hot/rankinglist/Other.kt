package cn.spacexc.wearbili.dataclass.hot.rankinglist

import cn.spacexc.wearbili.dataclass.video.Owner
import cn.spacexc.wearbili.dataclass.video.Rights
import cn.spacexc.wearbili.dataclass.video.Stat

data class Other(
    val aid: Int,
    val attribute: Int,
    val attribute_v2: Int?,
    val bvid: String,
    val cid: Int,
    val copyright: Int,
    val ctime: Int,
    val desc: String,
    val dimension: Dimension,
    val duration: Int,
    val `dynamic`: String,
    val first_frame: String,
    val mission_id: Int?,
    val owner: Owner,
    val pic: String,
    val pub_location: String,
    val pubdate: Int,
    val rights: Rights,
    val score: Int,
    val season_id: Int?,
    val short_link: String,
    val short_link_v2: String,
    val stat: Stat,
    val state: Int,
    val tid: Int,
    val title: String,
    val tname: String,
    val up_from_v2: Int?,
    val videos: Int
)