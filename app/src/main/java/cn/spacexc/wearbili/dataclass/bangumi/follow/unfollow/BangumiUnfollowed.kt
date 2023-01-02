package cn.spacexc.wearbili.dataclass.bangumi.follow.unfollow


import com.google.gson.annotations.SerializedName

data class BangumiUnfollowed(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("result")
    val result: Result
)