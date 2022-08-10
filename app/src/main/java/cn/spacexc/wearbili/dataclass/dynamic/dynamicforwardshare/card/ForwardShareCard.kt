package cn.spacexc.wearbili.dataclass.dynamic.dynamicforwardshare.card

data class ForwardShareCard(
    val activity_infos: ActivityInfos,
    val item: Item,
    val origin: String,
    var originObj: Any? = null,
    val origin_extend_json: String,
    val origin_user: OriginUser?,
    val user: User
)