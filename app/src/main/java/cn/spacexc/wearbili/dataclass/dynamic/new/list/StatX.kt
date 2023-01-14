package cn.spacexc.wearbili.dataclass.dynamic.new.list


import com.google.gson.annotations.SerializedName

data class StatX(
    @SerializedName("danmaku")
    val danmaku: String,
    @SerializedName("play")
    val play: String
)