package cn.spacexc.wearbili.dataclass.comment

import cn.spacexc.wearbili.dataclass.CommentContentData
import cn.spacexc.wearbili.dataclass.EmoteObject

data class Data(
    val dialog: Int,
    val dialog_str: String,
    val emote: Map<String, EmoteObject>,
    val need_captcha: Boolean,
    val parent: Int,
    val parent_str: String,
    val reply: CommentContentData,
    val root: Int,
    val root_str: String,
    val rpid: Long,
    val rpid_str: String,
    val success_action: Int,
    val success_toast: String,
    val url: String
)