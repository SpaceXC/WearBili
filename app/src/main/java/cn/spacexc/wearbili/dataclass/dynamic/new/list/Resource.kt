package cn.spacexc.wearbili.dataclass.dynamic.new.list


import com.google.gson.annotations.SerializedName

data class Resource(
    @SerializedName("res_image")
    val resImage: ResImage,
    @SerializedName("res_type")
    val resType: Int
)