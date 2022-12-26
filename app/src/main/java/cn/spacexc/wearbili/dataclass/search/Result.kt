package cn.spacexc.wearbili.dataclass.search


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("data")
    val `data`: List<Any>,
    @SerializedName("result_type")
    val resultType: String
)