package cn.spacexc.wearbili.dataclass.video.rcmd.web


import com.google.gson.annotations.SerializedName

data class Owner(
    @SerializedName("face")
    val face: String,
    @SerializedName("mid")
    val mid: Long,
    @SerializedName("name")
    val name: String
)