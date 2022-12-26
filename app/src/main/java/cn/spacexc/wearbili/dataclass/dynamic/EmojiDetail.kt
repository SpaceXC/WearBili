package cn.spacexc.wearbili.dataclass.dynamic

data class EmojiDetail(
    val attr: Int,
    val emoji_name: String,
    val id: Long,
    val meta: Meta,
    val mtime: Int,
    val package_id: Long,
    val state: Int,
    val text: String,
    val type: Int,
    val url: String
)