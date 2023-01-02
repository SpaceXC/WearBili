package cn.spacexc.wearbili.dataclass.bangumi.follow.follow


import com.google.gson.annotations.SerializedName

data class BangumiFollowed(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("result")
    val result: Result
)