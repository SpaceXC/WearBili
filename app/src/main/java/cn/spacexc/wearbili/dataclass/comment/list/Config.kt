package cn.spacexc.wearbili.dataclass.comment.list


import com.google.gson.annotations.SerializedName

data class Config(
    @SerializedName("read_only")
    val readOnly: Boolean,
    @SerializedName("show_up_flag")
    val showUpFlag: Boolean,
    @SerializedName("showtopic")
    val showtopic: Int
)