package cn.spacexc.wearbili.dataclass.comment.list


import com.google.gson.annotations.SerializedName

data class CardLabel(
    @SerializedName("background")
    val background: String,
    @SerializedName("background_height")
    val backgroundHeight: Int,
    @SerializedName("background_width")
    val backgroundWidth: Int,
    @SerializedName("effect")
    val effect: Int,
    @SerializedName("effect_start_time")
    val effectStartTime: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("jump_url")
    val jumpUrl: String,
    @SerializedName("label_color_day")
    val labelColorDay: String,
    @SerializedName("label_color_night")
    val labelColorNight: String,
    @SerializedName("rpid")
    val rpid: Long,
    @SerializedName("text_color_day")
    val textColorDay: String,
    @SerializedName("text_color_night")
    val textColorNight: String,
    @SerializedName("text_content")
    val textContent: String,
    @SerializedName("type")
    val type: Int
)