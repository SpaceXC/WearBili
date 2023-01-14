package cn.spacexc.wearbili.dataclass.dynamic.new.list


import com.google.gson.annotations.SerializedName

data class ImageSrc(
    @SerializedName("local")
    val local: Int?,
    @SerializedName("placeholder")
    val placeholder: Int?,
    @SerializedName("remote")
    val remote: Remote?,
    @SerializedName("src_type")
    val srcType: Int
)