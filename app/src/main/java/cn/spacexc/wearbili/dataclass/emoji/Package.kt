package cn.spacexc.wearbili.dataclass.emoji

data class Package(
    val attr: Int,
    val emote: List<Emote>,
    val flags: FlagsX,
    val id: Int,
    val meta: MetaX,
    val mtime: Int,
    val text: String,
    val type: Int,
    val url: String
)