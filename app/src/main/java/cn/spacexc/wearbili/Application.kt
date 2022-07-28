package cn.spacexc.wearbili

import android.annotation.SuppressLint
import android.content.Context
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes


/**
 * Created by XC-Qan on 2022/6/7.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

@SuppressLint("StaticFieldLeak")
class Application : android.app.Application() {
    companion object {
        var context: Application? = null
        fun getContext(): Context {
            return context!!
        }

        fun getTag(): String {
            return "WearBiliTag"
        }

        const val TAG = "WearBiliTag"
        private const val versionCode = "0.8.9"
        private const val releaseType = "Rel-AL"
        val versionString = getAppVersion()

        private fun getAppVersion(): String {
            return try {
                val packageInfo = context?.packageManager?.getPackageInfo(context?.packageName!!, 0)
                packageInfo?.versionName ?: ""
            } catch (e: Exception) {
                ""
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        //println(EncryptUtils.rsaEncrypt("bc698cf225d7aee6", "-----BEGIN PUBLIC KEY-----MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCdScM09sZJqFPX7bvmB2y6i08JnbHsa0v4THafPbJN9NoaZ9Djz1LmeLkVlmWx1DwgHVW+K7LVWT5FV3johacVRuV98n37+RNntEK6SE82MPcl7fA++dmW2cLlAjsIIkrX+aIvvSGCuUfcWpWFy3YVDqhuHrnNDjdNcaefJIQHMW+sQIDAQAB-----END PUBLIC KEY-----"))
        //println(EncryptUtils.rsaEncrypt("cf26d092ea32c28a", "-----BEGIN PUBLIC KEY-----\\nMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDjb4V7EidX/ym28t2ybo0U6t0n\\n6p4ej8VjqKHg100va6jkNbNTrLQqMCQCAYtXMXXp2Fwkk6WR+12N9zknLjf+C9sx\\n/+l48mjUU8RqahiFD1XT/u2e0m2EN029OhCgkHx3Fc/KlFSIbak93EH/XlYis0w+\\nXl69GV6klzgxW6d2xQIDAQAB\\n-----END PUBLIC KEY-----\\n"))
        AppCenter.start(
            this, "0365b962-2310-4b53-ba45-d92d84171f57",
            Analytics::class.java, Crashes::class.java
        )
        //LeanCloud.initializeSecurely(this, "MAE7LopsPz1kgP3deSjzQ67g-gzGzoHsz", "https://mae7lops.lc-cn-n1-shared.com")
        //LeanCloud.initialize(this, "MAE7LopsPz1kgP3deSjzQ67g-gzGzoHsz", "VuirpQYiGiekok2L03M9NX4o", )
    }


}