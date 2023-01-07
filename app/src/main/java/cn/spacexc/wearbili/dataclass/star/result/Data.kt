package cn.spacexc.wearbili.dataclass.star.result


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("prompt")
    val prompt: Boolean
)