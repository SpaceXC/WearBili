package cn.spacexc.wearbili.dataclass.subtitle


import com.google.gson.annotations.SerializedName

data class Body(
    @SerializedName("content")
    val content: String,
    @SerializedName("from")
    val from: Double,
    @SerializedName("location")
    val location: Int,
    @SerializedName("to")
    val to: Double
)