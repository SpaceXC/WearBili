package cn.spacexc.wearbili.dataclass.dynamic.new.list


import com.google.gson.annotations.SerializedName

data class Uncheck(
    @SerializedName("icon_url")
    val iconUrl: String,
    @SerializedName("text")
    val text: String
)