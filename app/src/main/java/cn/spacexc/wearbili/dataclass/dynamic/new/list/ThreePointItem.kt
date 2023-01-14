package cn.spacexc.wearbili.dataclass.dynamic.new.list


import com.google.gson.annotations.SerializedName

data class ThreePointItem(
    @SerializedName("label")
    val label: String,
    @SerializedName("type")
    val type: String
)