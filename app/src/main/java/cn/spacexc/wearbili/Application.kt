/*
SAY NO TO SUICIDE PUBLIC LICENSE

Version 1.0, September 2019

Copyright (C) 2022 XC-Qan

Everyone is permitted to copy and distribute verbatim copies
of this license document.

TERMS AND CONDITIONS FOR USE, REPRODUCTION, MODIFICATION, AND DISTRIBUTION

  1. You can do anything with the original copy,
  whenever, whatever, no limitation.

  2. When you are in despair, just talk to someone you trust,
  someone you love. Getting help from your family, your friends,
  the police, the community, the public.

  3. Keep yourself alive and say no to suicide.
*/

package cn.spacexc.wearbili

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import cn.spacexc.wearbili.manager.UserManager
import cn.spacexc.wearbili.utils.ToastUtils
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
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(
            TAG, "\n" +
                    "SAY NO TO SUICIDE PUBLIC LICENSE \n" +
                    "\n" +
                    "Version 1.0, September 2019\n" +
                    "\n" +
                    "Copyright (C) 2022 XC-Qan\n" +
                    "\n" +
                    "Everyone is permitted to copy and distribute verbatim copies\n" +
                    "of this license document.\n" +
                    "\n" +
                    "TERMS AND CONDITIONS FOR USE, REPRODUCTION, MODIFICATION, AND DISTRIBUTION\n" +
                    "\n" +
                    "  1. You can do anything with the original copy, \n" +
                    "  whenever, whatever, no limitation.\n" +
                    "  \n" +
                    "  2. When you are in despair, just talk to someone you trust, \n" +
                    "  someone you love. Getting help from your family, your friends, \n" +
                    "  the police, the community, the public.\n" +
                    "  \n" +
                    "  3. Keep yourself alive and say no to suicide.\n" +
                    "\n"
        )
        context = this
        AppCenter.start(
            this, "0365b962-2310-4b53-ba45-d92d84171f57",
            Analytics::class.java, Crashes::class.java
        )
        //UserManager.loginWithPassword("480816699", "MangoLiu2190")
        UserManager.getUserSpaceDetail(480816699L)
    }

    override fun onTerminate() {
        super.onTerminate()
        ToastUtils.makeText("onTerminate").show()
    }
}