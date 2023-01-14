package cn.spacexc.wearbili.dataclass.dynamic.new.list


import com.google.gson.annotations.SerializedName

data class ModuleMore(
    @SerializedName("three_point_items")
    val threePointItems: List<ThreePointItem>
)