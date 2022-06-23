package cn.spacexc.wearbili.utils

/**
 * Created by XC-Qan on 2022/6/12.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */


object TimeUtils {
    fun secondToTime(second: Long?): String {
        if(second == null) return "00:00"
        var second = second
        val hours = second / 3600 //转换小时数
        second %= 3600 //剩余秒数
        val minutes = second / 60 //转换分钟
        second %= 60 //剩余秒数
        return if (hours > 0) {
            unitFormat(hours) + ":" + unitFormat(minutes) + ":" + unitFormat(second)
        } else {
            unitFormat(minutes) + ":" + unitFormat(second)
        }
    }

    private fun unitFormat(i: Long): String {
        return if (i in 0..9) "0$i" else "" + i
    }
}

