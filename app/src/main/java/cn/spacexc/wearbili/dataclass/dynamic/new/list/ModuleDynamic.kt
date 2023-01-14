package cn.spacexc.wearbili.dataclass.dynamic.new.list


import com.google.gson.annotations.SerializedName

data class ModuleDynamic(
    @SerializedName("additional")
    val additional: Additional?,
    @SerializedName("desc")
    val desc: Desc?,
    @SerializedName("major")
    val major: Major?,
    @SerializedName("topic")
    val topic: Topic?
)