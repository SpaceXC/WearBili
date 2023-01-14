package cn.spacexc.wearbili.dataclass.dynamic.new.list


import com.google.gson.annotations.SerializedName

data class Major(
    @SerializedName("archive")
    val archive: Archive?,
    @SerializedName("pgc")
    val pgc: PgcMajor,
    @SerializedName("draw")
    val draw: Draw?,
    @SerializedName("type")
    val type: String
)