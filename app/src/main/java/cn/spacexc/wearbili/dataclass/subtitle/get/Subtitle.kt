package cn.spacexc.wearbili.dataclass.subtitle.get


import com.google.gson.annotations.SerializedName

data class Subtitle(
    @SerializedName("allow_submit")
    val allowSubmit: Boolean,
    @SerializedName("list")
    val list: List<SubtitleInfoItem>
)