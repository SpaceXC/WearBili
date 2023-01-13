package cn.spacexc.wearbili.dataclass.comment.list


import com.google.gson.annotations.SerializedName

data class Reply(
    @SerializedName("action")
    val action: Int,
    @SerializedName("assist")
    val assist: Int,
    @SerializedName("attr")
    val attr: Int,
    @SerializedName("card_label")
    val cardLabel: List<CardLabel>,
    @SerializedName("content")
    val content: Content,
    @SerializedName("count")
    val count: Int,
    @SerializedName("ctime")
    val ctime: Int,
    @SerializedName("dialog")
    val dialog: Int,
    @SerializedName("dynamic_id_str")
    val dynamicIdStr: String,
    @SerializedName("fansgrade")
    val fansgrade: Int,
    @SerializedName("folder")
    val folder: Folder,
    @SerializedName("invisible")
    val invisible: Boolean,
    @SerializedName("like")
    val like: Int,
    @SerializedName("member")
    val member: Member,
    @SerializedName("mid")
    val mid: Int,
    @SerializedName("oid")
    val oid: Int,
    @SerializedName("parent")
    val parent: Int,
    @SerializedName("parent_str")
    val parentStr: String,
    @SerializedName("rcount")
    val rcount: Int,
    @SerializedName("replies")
    val replies: List<Reply>,
    @SerializedName("reply_control")
    val replyControl: ReplyControl,
    @SerializedName("root")
    val root: Int,
    @SerializedName("root_str")
    val rootStr: String,
    @SerializedName("rpid")
    val rpid: Long,
    @SerializedName("rpid_str")
    val rpidStr: String,
    @SerializedName("state")
    val state: Int,
    @SerializedName("type")
    val type: Int,
    @SerializedName("up_action")
    val upAction: UpAction
)