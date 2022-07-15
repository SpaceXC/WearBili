package cn.spacexc.wearbili.dataclass.dynamic

data class CommentInfo(
    val comment_ids: String,
    val comments: List<Comment>?,
    val emojis: List<Emoji>?
)