package cn.spacexc.wearbili.dataclass.hot.rankinglist

import cn.spacexc.wearbili.dataclass.video.Owner
import cn.spacexc.wearbili.dataclass.video.Rights
import cn.spacexc.wearbili.dataclass.video.Stat

data class RankingVideo(
    val aid: Long,
    val bvid: String,
    val cid: Long,
    val copyright: Int,
    val ctime: Int,
    val desc: String,
    val dimension: Dimension,
    val duration: Int,
    val `dynamic`: String,
    val first_frame: String,
    val mission_id: Long?,
    val others: List<Other>?,
    val owner: Owner,
    val pic: String,
    val premiere: Premiere?,
    val pub_location: String,
    val pubdate: Int,
    val rights: Rights,
    val score: Int,
    val season_id: Long?,
    val short_link: String,
    val short_link_v2: String,
    val stat: Stat,
    val state: Int,
    val tid: Long,
    val title: String,
    val tname: String,
    val up_from_v2: Int?,
    val videos: Int
)