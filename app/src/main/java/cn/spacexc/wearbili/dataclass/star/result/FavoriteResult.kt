package cn.spacexc.wearbili.dataclass.star.result


import com.google.gson.annotations.SerializedName

data class FavoriteResult(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String
)