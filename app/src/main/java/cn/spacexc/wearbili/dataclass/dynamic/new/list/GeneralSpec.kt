package cn.spacexc.wearbili.dataclass.dynamic.new.list


import com.google.gson.annotations.SerializedName

data class GeneralSpec(
    @SerializedName("pos_spec")
    val posSpec: PosSpec,
    @SerializedName("render_spec")
    val renderSpec: RenderSpec,
    @SerializedName("size_spec")
    val sizeSpec: SizeSpec
)