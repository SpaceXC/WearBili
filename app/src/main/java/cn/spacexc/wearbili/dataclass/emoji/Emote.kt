package cn.spacexc.wearbili.dataclass.emoji

data class Emote(
    val activity: Any,
    val attr: Int,
    val flags: Flags,
    val gif_url: String,
    val id: Int,
    val meta: Meta,
    val mtime: Int,
    val package_id: Int,
    val text: String,
    val type: Int,
    val url: String
)