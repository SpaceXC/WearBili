package cn.spacexc.wearbili.dataclass.dynamic.new.list


import com.google.gson.annotations.SerializedName

data class Modules(
    @SerializedName("module_author")
    val moduleAuthor: ModuleAuthor,
    @SerializedName("module_dynamic")
    val moduleDynamic: ModuleDynamic,
    @SerializedName("module_interaction")
    val moduleInteraction: ModuleInteraction?,
    @SerializedName("module_more")
    val moduleMore: ModuleMore,
    @SerializedName("module_stat")
    val moduleStat: ModuleStat
)