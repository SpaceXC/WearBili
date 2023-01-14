package cn.spacexc.wearbili.dataclass.dynamic.new.list


import com.google.gson.annotations.SerializedName

data class Badge(
    @SerializedName("bg_color")
    val bgColor: String,
    @SerializedName("color")
    val color: String,
    @SerializedName("text")
    val text: String
)