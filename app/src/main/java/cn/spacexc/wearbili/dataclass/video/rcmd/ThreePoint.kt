package cn.spacexc.wearbili.dataclass.video.rcmd

data class ThreePoint(
    val dislike_reasons: List<DislikeReason>,
    val feedbacks: List<Feedback>?,
    val watch_later: Int?
)