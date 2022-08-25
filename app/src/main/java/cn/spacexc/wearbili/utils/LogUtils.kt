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
    fun Any.log(description: String) {
        Log.d(TAG, "$description: $this")
    }

    fun Any.log() {
        Log.d(TAG, "$this")
    }

    fun <T> T.logWithGeneric(): T {
        Log.d(TAG, "logWithGeneric: $this")
        return this
    }
}