package cn.spacexc.wearbili.dataclass.comment.list


import com.google.gson.annotations.SerializedName

data class Vip(
    @SerializedName("accessStatus")
    val accessStatus: Int,
    @SerializedName("avatar_subscript")
    val avatarSubscript: Int,
    @SerializedName("dueRemark")
    val dueRemark: String,
    @SerializedName("label")
    val label: Label,
    @SerializedName("nickname_color")
    val nicknameColor: String,
    @SerializedName("themeType")
    val themeType: Int,
    @SerializedName("vipDueDate")
    val vipDueDate: Long,
    @SerializedName("vipStatus")
    val vipStatus: Int,
    @SerializedName("vipStatusWarn")
    val vipStatusWarn: String,
    @SerializedName("vipType")
    val vipType: Int
)