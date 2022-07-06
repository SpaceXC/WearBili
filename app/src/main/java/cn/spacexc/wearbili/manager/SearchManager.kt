package cn.spacexc.wearbili.manager

import cn.spacexc.wearbili.utils.NetworkUtils
import okhttp3.Callback

/**
 * Created by XC-Qan on 2022/6/29.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

object SearchManager {
    fun getHotSearch(callback: Callback) {
        NetworkUtils.getUrl("https://s.search.bilibili.com/main/hotword", callback)
    }

    fun getDefaultSearchContent(callback: Callback) {
        NetworkUtils.getUrl("https://api.bilibili.com/x/web-interface/search/default", callback)
    }
}