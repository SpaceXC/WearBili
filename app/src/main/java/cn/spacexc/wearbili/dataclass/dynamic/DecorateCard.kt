package cn.spacexc.wearbili.dataclass.dynamic

data class DecorateCard(
    val big_card_url: String,
    val card_type: Int,
    val card_type_name: String,
    val card_url: String,
    val expire_time: Int,
    val fan: Fan,
    val id: Int,
    val image_enhance: String,
    val item_id: Int,
    val item_type: Int,
    val jump_url: String,
    val mid: Long,
    val name: String,
    val uid: Int
)