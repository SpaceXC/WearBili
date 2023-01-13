package cn.spacexc.wearbili.dataclass.comment.list


import com.google.gson.annotations.SerializedName

data class UserSailing(
    @SerializedName("cardbg")
    val cardbg: Any?,
    @SerializedName("cardbg_with_focus")
    val cardbgWithFocus: Any?,
    @SerializedName("pendant")
    val pendant: Any?
)