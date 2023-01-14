package cn.spacexc.wearbili.dataclass.bangumi.follow.unfollow


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("fmid")
    val fmid: Long,
    @SerializedName("relation")
    val relation: Boolean,
    @SerializedName("status")
    val status: Int,
    @SerializedName("toast")
    val toast: String
)