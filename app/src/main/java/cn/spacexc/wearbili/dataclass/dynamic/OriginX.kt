package cn.spacexc.wearbili.dataclass.dynamic

import cn.spacexc.wearbili.dataclass.dynamic.dynamicforwardshare.ShowTip
import cn.spacexc.wearbili.dataclass.dynamic.dynamicimage.Relation

data class OriginX(
    val emoji_info: EmojiInfo,
    val relation: Relation,
    val show_tip: ShowTip,
    val topic_info: TopicInfo
)