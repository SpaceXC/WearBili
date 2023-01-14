package cn.spacexc.wearbili.dataclass.dynamic.new.list


import com.google.gson.annotations.SerializedName

data class Fan(
    @SerializedName("color")
    val color: String,
    @SerializedName("is_fan")
    val isFan: Boolean,
    @SerializedName("num_str")
    val numStr: String,
    @SerializedName("number")
    val number: Int
)