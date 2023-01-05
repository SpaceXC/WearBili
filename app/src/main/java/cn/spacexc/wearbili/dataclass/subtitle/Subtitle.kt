package cn.spacexc.wearbili.dataclass.subtitle


import com.google.gson.annotations.SerializedName

data class Subtitle(
    @SerializedName("font_color")
    val fontColor: String,
    @SerializedName("background_alpha")
    val backgroundAlpha: Float,
    @SerializedName("background_color")
    val backgroundColor: String,
    @SerializedName("body")
    val body: List<Body>
)