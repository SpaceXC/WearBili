package cn.spacexc.wearbili.dataclass.dynamic.new.list


import com.google.gson.annotations.SerializedName

data class ResImage(
    @SerializedName("image_src")
    val imageSrc: ImageSrc
)