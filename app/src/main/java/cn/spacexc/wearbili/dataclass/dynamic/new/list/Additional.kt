package cn.spacexc.wearbili.dataclass.dynamic.new.list


import com.google.gson.annotations.SerializedName

data class Additional(
    @SerializedName("reserve")
    val reserve: Reserve,
    @SerializedName("type")
    val type: String
)