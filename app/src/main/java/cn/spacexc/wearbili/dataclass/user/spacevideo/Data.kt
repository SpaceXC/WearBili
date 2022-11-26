package cn.spacexc.wearbili.dataclass.user.spacevideo

data class Data(
    val episodic_button: EpisodicButton,
    val gaia_data: Any?,
    val gaia_res_type: Int,
    val is_risk: Boolean,
    val list: VideoList,
    val page: Page
)