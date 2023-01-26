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

/* WearBili  Copyright (C) 2022  XC
This program is licensed under GPL Licence v3.
This program comes with ABSOLUTELY NO WARRANTY.
This is free software, and you are welcome to redistribute it under certain conditions.
*/

package cn.spacexc.wearbili

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
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
        Log.d(
            TAG,
            "不知道我还能做多久" +
                    "\n真的好累" +
                    "\n做这个东西" +
                    "\n以及" +
                    "\n活着" +
                    "\n不如早点去死" +
                    "\n上面那玩意看着跟笑话一样" +
                    "\n如果我真的死了" +
                    "\n说不定可以把源码打出来烧给我" +
                    "\n不过这份写的太垃圾了（笑" +
                    "\n好像没有什么必要" +
                    "\n写这个东西真的有什么意义吗" +
                    "\n根本就没什么人能看到" +
                    "\n话说这个能算是遗书吗" +
                    "\n如果是的话想写好久了" +
                    "\n每次都是在脑子里构思" +
                    "\n构思" +
                    "\n构思" +
                    "\n构思" +
                    "\n从来就没有动过笔或者开始写" +
                    "\n（好懒" +
                    "\n不过今天算是一时冲动写了点东西？" +
                    "\n过去" +
                    "\n好失败" +
                    "\n各种意义上都是" +
                    "\n就算活着也没有什么意义" +
                    "\n或许是对我来说" +
                    "\n本来还想死在学校里来着" +
                    "\n想想也比较麻烦" +
                    "\n不过也不是不可以" +
                    "\n还可以给他们见见活死人" +
                    "\n（什么鬼"
        )
        context = this
        AppCenter.start(
            this, "0365b962-2310-4b53-ba45-d92d84171f57",
            Analytics::class.java, Crashes::class.java
        )
    }

    override fun onTerminate() {
        super.onTerminate()
        ToastUtils.makeText("onTerminate").show()
    }
}