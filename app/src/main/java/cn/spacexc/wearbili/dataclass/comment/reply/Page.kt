package cn.spacexc.wearbili.dataclass.comment.reply


import com.google.gson.annotations.SerializedName

data class Page(
    @SerializedName("count")
    val count: Int,
    @SerializedName("num")
    val num: Int,
    @SerializedName("size")
    val size: Int
)