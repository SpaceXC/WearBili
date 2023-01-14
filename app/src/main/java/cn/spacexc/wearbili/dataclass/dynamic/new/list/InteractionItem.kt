package cn.spacexc.wearbili.dataclass.dynamic.new.list


import com.google.gson.annotations.SerializedName

data class InteractionItem(
    @SerializedName("desc")
    val desc: Desc,
    @SerializedName("type")
    val type: Int
)