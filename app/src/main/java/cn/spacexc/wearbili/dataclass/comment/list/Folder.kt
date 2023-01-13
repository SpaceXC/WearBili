package cn.spacexc.wearbili.dataclass.comment.list


import com.google.gson.annotations.SerializedName

data class Folder(
    @SerializedName("has_folded")
    val hasFolded: Boolean,
    @SerializedName("is_folded")
    val isFolded: Boolean,
    @SerializedName("rule")
    val rule: String
)