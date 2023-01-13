package cn.spacexc.wearbili.dataclass.comment.list


import com.google.gson.annotations.SerializedName

data class OfficialVerify(
    @SerializedName("desc")
    val desc: String,
    @SerializedName("type")
    val type: Int
)