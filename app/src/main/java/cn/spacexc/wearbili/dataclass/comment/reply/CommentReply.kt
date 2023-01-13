package cn.spacexc.wearbili.dataclass.comment.reply


import com.google.gson.annotations.SerializedName

data class CommentReply(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("ttl")
    val ttl: Int
)