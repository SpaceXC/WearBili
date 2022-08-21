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
data class SettingItem(
    val type: SettingType,
    val settingName: String,
    val displayName: String,
    val iconRes: Int,
    val color: String = "#FFFFFF",
    val defString: String = "",
    val defBool: Boolean = false,
    val description: String,
    val action: () -> Unit = {},
    val chooseItems: List<ChooseItem> = emptyList(),
    val categoryItems: List<SettingItem> = emptyList(),
    val requireSave: Boolean,
) : Parcelable