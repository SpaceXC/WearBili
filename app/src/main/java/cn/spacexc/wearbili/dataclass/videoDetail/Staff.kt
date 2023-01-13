package cn.spacexc.wearbili.dataclass.videoDetail


import cn.spacexc.wearbili.dataclass.video.OfficialVerify
import cn.spacexc.wearbili.dataclass.video.Vip
import com.google.gson.annotations.SerializedName

data class Staff(
    @SerializedName("attention")
    val attention: Int,
    @SerializedName("face")
    val face: String,
    @SerializedName("mid")
    val mid: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("official_verify")
    val officialVerify: OfficialVerify,
    @SerializedName("title")
    val title: String,
    @SerializedName("vip")
    val vip: Vip
)