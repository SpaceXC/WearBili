package cn.spacexc.wearbili.dataclass.video.rcmd.web


import com.google.gson.annotations.SerializedName

data class RcmdReason(
    @SerializedName("content")
    val content: String?,
    @SerializedName("reason_type")
    val reasonType: Int
)