package cn.spacexc.wearbili.dataclass.comment.list


import com.google.gson.annotations.SerializedName

data class FansDetail(
    @SerializedName("guard_icon")
    val guardIcon: String,
    @SerializedName("guard_level")
    val guardLevel: Int,
    @SerializedName("honor_icon")
    val honorIcon: String,
    @SerializedName("intimacy")
    val intimacy: Int,
    @SerializedName("is_receive")
    val isReceive: Int,
    @SerializedName("level")
    val level: Int,
    @SerializedName("master_status")
    val masterStatus: Int,
    @SerializedName("medal_color")
    val medalColor: Int,
    @SerializedName("medal_color_border")
    val medalColorBorder: Int,
    @SerializedName("medal_color_end")
    val medalColorEnd: Int,
    @SerializedName("medal_color_level")
    val medalColorLevel: Long,
    @SerializedName("medal_color_name")
    val medalColorName: Long,
    @SerializedName("medal_id")
    val medalId: Int,
    @SerializedName("medal_level_bg_color")
    val medalLevelBgColor: Int,
    @SerializedName("medal_name")
    val medalName: String,
    @SerializedName("score")
    val score: Int,
    @SerializedName("uid")
    val uid: Int
)