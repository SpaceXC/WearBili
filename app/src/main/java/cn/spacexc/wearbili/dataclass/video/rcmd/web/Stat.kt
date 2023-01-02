package cn.spacexc.wearbili.dataclass.video.rcmd.web


import com.google.gson.annotations.SerializedName

data class Stat(
    @SerializedName("danmaku")
    val danmaku: Int,
    @SerializedName("like")
    val like: Int,
    @SerializedName("view")
    val view: Int
)