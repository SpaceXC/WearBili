package cn.spacexc.wearbili.dataclass.dynamic.new.list


import com.google.gson.annotations.SerializedName

data class ModuleInteraction(
    @SerializedName("items")
    val items: List<InteractionItem>
)