package cn.spacexc.wearbili.dataclass.settings

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by XC-Qan on 2022/8/18.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

@Parcelize
data class ChooseItem(
    val name: String,
    val displayName: String,
    val icon: Int,
    val description: String
) : Parcelable
