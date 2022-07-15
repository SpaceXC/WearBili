package cn.spacexc.wearbili.dataclass.dynamic.dynamicforwardshare

data class OriginX(
    val biz_info: BizInfo,
    val cover_play_icon_url: String,
    val relation: RelationX,
    val show_tip: ShowTip,
    val tags: List<Tag>,
    val topic_info: TopicInfo,
    val usr_action_txt: String
)