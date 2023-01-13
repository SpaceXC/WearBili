package cn.spacexc.wearbili.dataclass.comment.list


import com.google.gson.annotations.SerializedName

data class Cursor(
    @SerializedName("all_count")
    val allCount: Int,
    @SerializedName("is_begin")
    val isBegin: Boolean,
    @SerializedName("is_end")
    val isEnd: Boolean,
    @SerializedName("mode")
    val mode: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("next")
    val next: Int,
    @SerializedName("prev")
    val prev: Int,
    @SerializedName("support_mode")
    val supportMode: List<Int>
)