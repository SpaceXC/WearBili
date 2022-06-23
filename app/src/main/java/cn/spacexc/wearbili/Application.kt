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
        var context:Application? = null
        fun getContext():Context{
            return context!!
        }
        fun getTag() : String{
            return "WearBiliTag"
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        AppCenter.start(
            this, "0365b962-2310-4b53-ba45-d92d84171f57",
            Analytics::class.java, Crashes::class.java
        )
    }
}