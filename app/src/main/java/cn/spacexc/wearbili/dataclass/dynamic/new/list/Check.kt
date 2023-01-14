package cn.spacexc.wearbili.dataclass.dynamic.new.list


import com.google.gson.annotations.SerializedName

data class Check(
    @SerializedName("icon_url")
    val iconUrl: String,
    @SerializedName("text")
    val text: String
)