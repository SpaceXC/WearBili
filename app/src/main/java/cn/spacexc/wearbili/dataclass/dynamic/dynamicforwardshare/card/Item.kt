package cn.spacexc.wearbili.dataclass.dynamic.dynamicforwardshare.card

data class Item(
    val content: String,
    val ctrl: String,
    val orig_dy_id: Long,
    val orig_type: Int,
    val pre_dy_id: Long,
    val reply: Int,
    val rp_id: Long,
    val timestamp: Int,
    val uid: Int
)