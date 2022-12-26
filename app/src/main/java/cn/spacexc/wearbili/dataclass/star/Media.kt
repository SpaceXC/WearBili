package cn.spacexc.wearbili.dataclass.star

data class Media(
    val attr: Int,
    val bv_id: String,
    val bvid: String,
    val cnt_info: CntInfo,
    val cover: String,
    val ctime: Int,
    val duration: Int,
    val fav_time: Int,
    val id: Long,
    val intro: String,
    val link: String,
    val ogv: Any?,
    val page: Int,
    val pubtime: Int,
    val season: Any?,
    val title: String,
    val type: Int,
    val ugc: Ugc,
    val upper: Upper
)