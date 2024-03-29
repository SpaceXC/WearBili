package cn.spacexc.wearbili.utils

import android.util.Log
import cn.spacexc.wearbili.Application.Companion.TAG

/**
 * Created by XC-Qan on 2022/8/11.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

object LogUtils {
    fun <T> T.log(): T {
        Log.d(TAG, "$this")
        return this
    }

    fun <T> T.log(description: String): T {
        Log.d(TAG, "$description: $this")
        return this
    }
}