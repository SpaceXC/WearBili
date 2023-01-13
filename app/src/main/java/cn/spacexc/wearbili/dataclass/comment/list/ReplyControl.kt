package cn.spacexc.wearbili.dataclass.comment.list


import com.google.gson.annotations.SerializedName

data class ReplyControl(
    @SerializedName("location")
    val location: String,
    @SerializedName("max_line")
    val maxLine: Int,
    @SerializedName("time_desc")
    val timeDesc: String
)