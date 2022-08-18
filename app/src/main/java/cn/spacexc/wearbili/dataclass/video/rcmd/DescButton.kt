package cn.spacexc.wearbili.dataclass.video.rcmd

data class DescButton(
    val event: String,
    val event_v2: String,
    val text: String,
    val type: Int,
    val uri: String?
)