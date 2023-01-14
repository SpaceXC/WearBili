package cn.spacexc.wearbili.dataclass.dynamic.new.list


import com.google.gson.annotations.SerializedName

data class Forward(
    @SerializedName("count")
    val count: Int,
    @SerializedName("forbidden")
    val forbidden: Boolean
)