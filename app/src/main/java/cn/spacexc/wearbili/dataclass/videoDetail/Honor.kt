package cn.spacexc.wearbili.dataclass.videoDetail


import com.google.gson.annotations.SerializedName

data class Honor(
    @SerializedName("bg_color")
    val bgColor: String,
    @SerializedName("bg_color_night")
    val bgColorNight: String,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("icon_night")
    val iconNight: String,
    @SerializedName("text")
    val text: String,
    @SerializedName("text_color")
    val textColor: String,
    @SerializedName("text_color_night")
    val textColorNight: String,
    @SerializedName("text_extra")
    val textExtra: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("url_text")
    val urlText: String
)