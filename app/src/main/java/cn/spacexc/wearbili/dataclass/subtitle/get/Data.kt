package cn.spacexc.wearbili.dataclass.subtitle.get


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("subtitle")
    val subtitle: Subtitle
)