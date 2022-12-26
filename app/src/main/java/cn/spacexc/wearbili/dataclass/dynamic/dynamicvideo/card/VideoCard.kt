package cn.spacexc.wearbili.dataclass.dynamic.dynamicvideo.card

data class VideoCard(
    val aid: Long,
    val attribute: Int,
    val cid: Long,
    val copyright: Int,
    val ctime: Int,
    val desc: String,
    val dimension: Dimension,
    val duration: Int,
    val `dynamic`: String?,
    val first_frame: String,
    val jump_url: String,
    val mission_id: Long,
    val owner: Owner,
    val pic: String,
    val player_info: Any?,
    val pubdate: Int,
    val rights: Rights,
    val short_link: String,
    val short_link_v2: String,
    val stat: Stat,
    val state: Int,
    val tid: Long,
    val title: String,
    val tname: String,
    val videos: Int
)