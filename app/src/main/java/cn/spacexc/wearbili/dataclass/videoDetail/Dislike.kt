package cn.spacexc.wearbili.dataclass.videoDetail

data class Dislike(
    val closed_paste_text: String,
    val closed_toast: String,
    val dislike_reason: List<DislikeReasonX>,
    val paste_text: String,
    val sub_title: String,
    val title: String,
    val toast: String
)