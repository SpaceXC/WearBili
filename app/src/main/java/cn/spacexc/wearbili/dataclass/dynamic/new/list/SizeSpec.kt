package cn.spacexc.wearbili.dataclass.dynamic.new.list


import com.google.gson.annotations.SerializedName

data class SizeSpec(
    @SerializedName("height")
    val height: Double,
    @SerializedName("width")
    val width: Double
)