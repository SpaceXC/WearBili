package cn.spacexc.wearbili.dataclass.comment.list


import com.google.gson.annotations.SerializedName

data class Content(
    @SerializedName("emote")
    val emote: Map<String, Emotion>,
    @SerializedName("max_line")
    val maxLine: Int,
    @SerializedName("members")
    val members: List<Any>,
    @SerializedName("message")
    val message: String
)