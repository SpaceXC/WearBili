package cn.spacexc.wearbili.dataclass.video.rcmd.web


import com.google.gson.annotations.SerializedName

data class FloorInfo(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("rows")
    val rows: Int
)