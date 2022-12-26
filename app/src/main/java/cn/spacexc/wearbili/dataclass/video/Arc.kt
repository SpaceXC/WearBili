package cn.spacexc.wearbili.dataclass.video

data class Arc(
    val aid: Long,
    val author: AuthorX,
    val copyright: Int,
    val ctime: Int,
    val desc: String,
    val desc_v2: Any?,
    val dimension: Dimension,
    val duration: Int,
    val `dynamic`: String,
    val is_blooper: Boolean,
    val is_chargeable_season: Boolean,
    val pic: String,
    val pubdate: Int,
    val rights: RightsX,
    val stat: StatX,
    val state: Int,
    val title: String,
    val type_id: Long,
    val type_name: String,
    val videos: Int
)