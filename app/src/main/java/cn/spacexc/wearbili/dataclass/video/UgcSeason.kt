package cn.spacexc.wearbili.dataclass.video

data class UgcSeason(
    val attribute: Int,
    val cover: String,
    val ep_count: Int,
    val id: Int,
    val intro: String,
    val is_pay_season: Boolean,
    val mid: Long,
    val season_type: Int,
    val sections: List<Section>,
    val sign_state: Int,
    val stat: StatXX,
    val title: String
)