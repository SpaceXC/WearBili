package cn.spacexc.wearbili.dataclass.video.rcmd

data class Video(
    val auto_play: Boolean,
    val auto_play_value: Int,
    val avid: Int,
    val biz_id: Int,
    val btn_dyc_color: Boolean,
    val btn_dyc_time: Int,
    val cid: Int,
    val cover: String,
    val egg_end_time: Int,
    val egg_start_time: Int,
    val ep_id: Int,
    val from: String,
    val from_spmid: String,
    val orientation: Int,
    val page: Int,
    val press_trigger_time: Int,
    val season_id: Int,
    val url: String
)