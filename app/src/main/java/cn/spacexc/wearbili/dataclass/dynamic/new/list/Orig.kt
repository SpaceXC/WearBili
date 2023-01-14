package cn.spacexc.wearbili.dataclass.dynamic.new.list


import com.google.gson.annotations.SerializedName

data class Orig(
    @SerializedName("basic")
    val basic: Basic,
    @SerializedName("id_str")
    val idStr: String,
    @SerializedName("modules")
    val modules:
    OriginModules,
    @SerializedName("type")
    val type: String,
    @SerializedName("visible")
    val visible: Boolean
)