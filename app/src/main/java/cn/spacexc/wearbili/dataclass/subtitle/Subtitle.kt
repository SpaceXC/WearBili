package cn.spacexc.wearbili.dataclass.subtitle


import com.google.gson.annotations.SerializedName

data class Subtitle(
    @SerializedName("body")
    val body: List<Body>
)