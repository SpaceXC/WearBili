package cn.spacexc.wearbili.dataclass.dynamic.new.list


import com.google.gson.annotations.SerializedName

data class ModuleStat(
    @SerializedName("comment")
    val comment: Comment,
    @SerializedName("forward")
    val forward: Forward,
    @SerializedName("like")
    val like: Like
)