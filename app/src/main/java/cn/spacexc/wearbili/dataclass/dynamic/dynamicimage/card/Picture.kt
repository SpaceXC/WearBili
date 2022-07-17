package cn.spacexc.wearbili.dataclass.dynamic.dynamicimage.card

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Picture(
    val img_height: Int,
    val img_size: Double,
    val img_src: String,
    val img_width: Int
) : Parcelable