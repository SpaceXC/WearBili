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
object SharedPreferencesUtils {
    private const val PREFS_NAME = "cn.spacexc.wearbili.sharedpreferences"
    private val preferences = Application.getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val editor = preferences.edit()


    fun getString(key: String?, defaultValue: String?): String? {
        return preferences.getString(key, defaultValue)
    }

    fun getInt(key: String?, defaultValue: Int?): Int {
        return preferences.getInt(key, defaultValue!!)
    }

    fun getBoolean(key: String?, defaultValue: Boolean?): Boolean {
        return preferences.getBoolean(key, defaultValue!!)
    }

    fun saveString(key: String?, value: String?) {
        editor.putString(key, value)
        editor.commit()
    }

    fun saveInt(key: String?, value: Int?) {
        editor.putInt(key, value!!)
        editor.commit()
    }

    fun saveBool(key: String?, value: Boolean?) {
        editor.putBoolean(key, value!!)
        editor.commit()
    }

    fun delete(key: String?) {
        editor.remove(key)
        editor.commit()
    }
}