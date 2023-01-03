package cn.spacexc.wearbili.dataclass.subtitle.get


import com.google.gson.annotations.SerializedName

data class SubtitleInfo(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("ttl")
    val ttl: Int
)