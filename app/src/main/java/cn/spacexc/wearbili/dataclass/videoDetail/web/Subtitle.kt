package cn.spacexc.wearbili.dataclass.videoDetail.web

import cn.spacexc.wearbili.dataclass.subtitle.get.SubtitleInfoItem

data class Subtitle(
    val allow_submit: Boolean,
    val list: List<SubtitleInfoItem>
)