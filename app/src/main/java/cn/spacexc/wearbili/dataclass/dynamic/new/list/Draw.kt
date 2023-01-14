package cn.spacexc.wearbili.dataclass.dynamic.new.list


import com.google.gson.annotations.SerializedName

data class Draw(
    @SerializedName("id")
    val id: Long,
    @SerializedName("items")
    val items: List<DrawItem>
)