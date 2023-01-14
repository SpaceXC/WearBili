package cn.spacexc.wearbili.dataclass.dynamic.new.list


import com.google.gson.annotations.SerializedName

data class ContainerSize(
    @SerializedName("height")
    val height: Double,
    @SerializedName("width")
    val width: Double
)