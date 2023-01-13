package cn.spacexc.wearbili.dataclass.comment.list


import com.google.gson.annotations.SerializedName

data class Emotion(
    @SerializedName("attr")
    val attr: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("jump_title")
    val jumpTitle: String,
    @SerializedName("meta")
    val meta: MetaX,
    @SerializedName("mtime")
    val mtime: Int,
    @SerializedName("package_id")
    val packageId: Int,
    @SerializedName("state")
    val state: Int,
    @SerializedName("text")
    val text: String,
    @SerializedName("type")
    val type: Int,
    @SerializedName("url")
    val url: String
)