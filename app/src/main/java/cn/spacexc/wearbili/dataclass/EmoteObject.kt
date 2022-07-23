package cn.spacexc.wearbili.dataclass

data class EmoteObject(
    val attr: Int,
    val id: Int,
    val jump_title: String,
    val meta: Meta,
    val mtime: Int,
    val package_id: Int,
    val state: Int,
    val text: String,
    val type: Int,
    val url: String
)