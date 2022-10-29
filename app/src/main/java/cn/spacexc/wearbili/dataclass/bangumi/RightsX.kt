package cn.spacexc.wearbili.dataclass.bangumi

data class RightsX(
    val allow_bp: Int,
    val allow_bp_rank: Int,
    val allow_download: Int,
    val allow_review: Int,
    val area_limit: Int,
    val ban_area_show: Int,
    val can_watch: Int,
    val copyright: String,
    val forbid_pre: Int,
    val freya_white: Int,
    val is_cover_show: Int,
    val is_preview: Int,
    val only_vip_download: Int,
    val resource: String,
    val watch_platform: Int
)