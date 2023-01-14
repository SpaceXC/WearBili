package cn.spacexc.wearbili.dataclass.dynamic.new.list


import com.google.gson.annotations.SerializedName

data class PosSpec(
    @SerializedName("axis_x")
    val axisX: Double,
    @SerializedName("axis_y")
    val axisY: Double,
    @SerializedName("coordinate_pos")
    val coordinatePos: Int
)