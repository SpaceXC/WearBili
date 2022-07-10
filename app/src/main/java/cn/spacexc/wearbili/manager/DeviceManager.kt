package cn.spacexc.wearbili.manager

import android.os.Build
import java.util.*

/**
 * From CSDN
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

object DeviceManager {
    /**
     * 设备名称
     *
     * @return 设备名称
     */
    fun getDeviceName(): String? {
        return Build.DEVICE
    }

    /**
     * 设备型号
     *
     * @return 设备型号
     */
    fun getModelName(): String? {
        return Build.MODEL
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    fun getSystemVersion(): String? {
        return Build.VERSION.RELEASE
    }

    /**
     * 获取厂商
     *
     * @return 厂商
     */
    fun getBrand(): String? {
        return Build.BRAND
    }

    /**
     * 获取设备制造商
     *
     * @return 制造商
     */
    fun getManufacturer(): String? {
        return Build.MANUFACTURER
    }

    /**
     * SDK 版本
     * @return
     */
    fun getSDKVersion(): String {
        return Build.VERSION.SDK_INT.toString()
    }

    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    fun getSystemLanguage(): String? {
        return Locale.getDefault().getLanguage()
    }

    /**
     * 获取当前系统上的语言列表(Locale列表)
     *
     * @return  语言列表
     */
    fun getSystemLanguageList(): Array<Locale?>? {
        return Locale.getAvailableLocales()
    }


}