package cn.spacexc.wearbili.exception

import cn.spacexc.wearbili.utils.ToastUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/* 
WearBili Copyright (C) 2023 XC
This program comes with ABSOLUTELY NO WARRANTY.
This is free software, and you are welcome to redistribute it under certain conditions.
*/

/*
 * Created by XC on 2023/1/14.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class IllegalDataReturnException(
    private val url: String,
    private val dataMessage: String,
    val code: Int
) :
    Exception("The server api $url is returning a data with code $code, witch is illegal here and can't be processed. The error message has been presented to the user: $dataMessage") {
    override fun printStackTrace() {
        super.printStackTrace()
        MainScope().launch {
            ToastUtils.showText("$code: $dataMessage")
        }
    }
}