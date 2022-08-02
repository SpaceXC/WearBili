package cn.spacexc.wearbili.dataclass.dynamic

data class Display(
    val add_on_card_info: List<AddOnCardInfo>?,
    val biz_info: BizInfo?,
    val like_infoL: LikeInfo,
    val comment_info: CommentInfo,
    val cover_play_icon_url: String?,
    val emoji_info: EmojiInfo?,
    val origin: OriginX?,
    val relation: RelationX,
    val show_tip: ShowTipX?,
    val topic_info: TopicInfoX?,
    val usr_action_txt: String?
)