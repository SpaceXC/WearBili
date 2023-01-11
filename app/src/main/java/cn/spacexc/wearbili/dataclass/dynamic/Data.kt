package cn.spacexc.wearbili.dataclass.dynamic

data class Data(
    val _gt_: Int,
    val attentions: Attentions,
    val has_more: Int?,
    val cards: List<Card>?,
    val exist_gap: Int,
    val history_offset: Long,
    val max_dynamic_id: Long,
    val new_num: Int,
    val open_rcmd: Int,
    val update_num: Int
)