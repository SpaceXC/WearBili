package cn.spacexc.wearbili.utils

import android.content.Context
import cn.spacexc.wearbili.Application

/**
 * Created by XC-Qan on 2022/6/7.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */
//FIXME 给爷换成数据库！（谁教你这么用shp的.jpg

object SubtitleSharedPreferencesUtils {
    private const val PREFS_NAME = "cn.spacexc.wearbili.sharedpreferences.subtitle"
    private val preferences =
        Application.getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val editor = preferences.edit()

    fun getUrl(cid: String): String? {
        return preferences.getString(cid, null)
    }

    fun saveUrl(cid: String, url: String) {
        editor.putString(cid, url)
        editor.commit()
    }

    fun contains(cid: String): Boolean {
        return preferences.contains(cid)
    }
}