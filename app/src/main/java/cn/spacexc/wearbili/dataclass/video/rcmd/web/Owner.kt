package cn.spacexc.wearbili.dataclass.video.rcmd.web


import com.google.gson.annotations.SerializedName

data class Owner(
    @SerializedName("face")
    val face: String,
    @SerializedName("mid")
    val mid: Int,
    @SerializedName("name")
    val name: String
)