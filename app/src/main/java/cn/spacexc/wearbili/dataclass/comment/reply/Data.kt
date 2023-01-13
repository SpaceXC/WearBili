package cn.spacexc.wearbili.dataclass.comment.reply


import cn.spacexc.wearbili.dataclass.CommentContentData
import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("page")
    val page: Page,
    @SerializedName("show_bvid")
    val showBvid: Boolean,
    @SerializedName("replies")
    val replies: List<CommentContentData>?,
    @SerializedName("root")
    val root: CommentContentData,
    @SerializedName("show_text")
    val showText: String,
    @SerializedName("show_type")
    val showType: Int,
    @SerializedName("upper")
    val upper: Upper
)