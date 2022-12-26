package cn.spacexc.wearbili.dataclass.dynamic.dynamicimage.card

data class Item(
    val at_control: String,
    val category: String,
    val description: String?,
    val id: Long,
    val is_fav: Int,
    val pictures: List<Picture>,
    val pictures_count: Int,
    val reply: Int,
    val role: List<Any>,
    val settings: Settings,
    val source: List<Any>,
    val title: String,
    val upload_time: Int
)