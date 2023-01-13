package cn.spacexc.wearbili.dataclass.comment.list


import com.google.gson.annotations.SerializedName

data class Pendant(
    @SerializedName("expire")
    val expire: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("image_enhance")
    val imageEnhance: String,
    @SerializedName("image_enhance_frame")
    val imageEnhanceFrame: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("pid")
    val pid: Int
)