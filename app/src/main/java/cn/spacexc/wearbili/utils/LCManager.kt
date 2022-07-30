package cn.spacexc.wearbili.utils

import okhttp3.Callback

/**
 * Created by XC-Qan on 2022/7/30.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

object LCManager {
    fun isUserActivated(uid: String, callback: Callback) {
        NetworkUtils.getUrlWithLC(
            "https://mae7lops.lc-cn-n1-shared.com/1.1/classes/ActivatedUIDs?where={\"uid\":\"$uid\"}",
            callback
        )
    }
}