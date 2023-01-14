package cn.spacexc.wearbili.dataclass.dynamic.new.list


import com.google.gson.annotations.SerializedName

data class DynamicItem(
    @SerializedName("basic")
    val basic: Basic,
    @SerializedName("id_str")
    val idStr: String,
    @SerializedName("modules")
    val modules: Modules,
    @SerializedName("orig")
    val orig: Orig?,
    @SerializedName("type")
    val type: String,
    @SerializedName("visible")
    val visible: Boolean
)