package cn.spacexc.wearbili.dataclass.history

data class HistoryObject(
    val author_face: String,
    val author_mid: Long,
    val author_name: String,
    val badge: String,
    val cover: String?,
    val covers: List<String>,
    val current: String,
    val duration: Int,
    val history: HistoryX,
    val is_fav: Int,
    val is_finish: Int,
    val kid: Int,
    val live_status: Int,
    val long_title: String,
    val new_desc: String,
    val progress: Int,
    val show_title: String,
    val tag_name: String,
    val title: String,
    val total: Int,
    val uri: String,
    val videos: Int,
    val view_at: Long
)