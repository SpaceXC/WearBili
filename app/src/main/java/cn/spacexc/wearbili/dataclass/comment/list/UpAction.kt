package cn.spacexc.wearbili.dataclass.comment.list


import com.google.gson.annotations.SerializedName

data class UpAction(
    @SerializedName("like")
    val like: Boolean,
    @SerializedName("reply")
    val reply: Boolean
)