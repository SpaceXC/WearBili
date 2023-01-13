package cn.spacexc.wearbili.dataclass.comment.list


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("assist")
    val assist: Int,
    @SerializedName("blacklist")
    val blacklist: Int,
    @SerializedName("config")
    val config: Config,
    @SerializedName("control")
    val control: Control,
    @SerializedName("cursor")
    val cursor: Cursor,
    @SerializedName("note")
    val note: Int,
    @SerializedName("replies")
    val replies: List<Reply>,
    @SerializedName("top_replies")
    val topReplies: List<TopReply>,
    @SerializedName("upper")
    val upper: Upper,
    @SerializedName("vote")
    val vote: Int
)