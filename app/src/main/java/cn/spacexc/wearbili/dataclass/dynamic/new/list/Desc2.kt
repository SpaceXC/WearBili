package cn.spacexc.wearbili.dataclass.dynamic.new.list


import com.google.gson.annotations.SerializedName

data class Desc2(
    @SerializedName("style")
    val style: Int,
    @SerializedName("text")
    val text: String,
    @SerializedName("visible")
    val visible: Boolean
)