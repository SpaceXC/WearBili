package cn.spacexc.wearbili.dataclass.subtitle.get


import com.google.gson.annotations.SerializedName

data class SubtitleInfoItem(
    @SerializedName("ai_status")
    val aiStatus: Int,
    @SerializedName("ai_type")
    val aiType: Int,
    @SerializedName("author")
    val author: Author,
    @SerializedName("id")
    val id: Long,
    @SerializedName("id_str")
    val idStr: String,
    @SerializedName("is_lock")
    val isLock: Boolean,
    @SerializedName("lan")
    val lan: String,
    @SerializedName("lan_doc")
    val lanDoc: String,
    @SerializedName("subtitle_url")
    val subtitleUrl: String,
    @SerializedName("type")
    val type: Int
)