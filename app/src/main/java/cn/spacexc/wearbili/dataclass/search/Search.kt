package cn.spacexc.wearbili.dataclass.search


import com.google.gson.annotations.SerializedName

data class Search(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("ttl")
    val ttl: Int
)