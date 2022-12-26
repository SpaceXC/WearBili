package cn.spacexc.wearbili.dataclass.search.mediaft


import com.google.gson.annotations.SerializedName

data class MediaScore(
    @SerializedName("score")
    val score: Int,
    @SerializedName("user_count")
    val userCount: Int
)