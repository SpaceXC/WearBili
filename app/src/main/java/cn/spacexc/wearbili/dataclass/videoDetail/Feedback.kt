package cn.spacexc.wearbili.dataclass.videoDetail

data class Feedback(
    val closed_paste_text: String,
    val closed_sub_title: String,
    val closed_toast: String,
    val dislike_reason: List<DislikeReason>,
    val paste_text: String,
    val sub_title: String,
    val title: String,
    val toast: String
)