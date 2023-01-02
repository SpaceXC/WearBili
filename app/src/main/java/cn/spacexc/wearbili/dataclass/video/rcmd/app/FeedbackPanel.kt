package cn.spacexc.wearbili.dataclass.video.rcmd.app

data class FeedbackPanel(
    val close_rec_tips: String,
    val feedback_panel_detail: List<FeedbackPanelDetail>,
    val open_rec_tips: String,
    val panel_type_text: String,
    val toast: String
)