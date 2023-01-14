package cn.spacexc.wearbili.dataclass.comment.list


import com.google.gson.annotations.SerializedName

data class Nameplate(
    @SerializedName("condition")
    val condition: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("image_small")
    val imageSmall: String,
    @SerializedName("level")
    val level: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("nid")
    val nid: Long
)