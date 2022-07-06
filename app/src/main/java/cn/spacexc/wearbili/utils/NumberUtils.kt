package cn.spacexc.wearbili.utils

/**
 * Created by XC-Qan on 2022/6/23.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

object NumberUtils {
    fun num2Chinese(num: Long): String {
        if (num <= 0) {
            return "0"
        }
        if (num < 10000) {
            return num.toString()
        }
        if (num in 10000..99999999) {
            val numStr = num.toString()
            val tenThousand = numStr.substring(0, numStr.length - 4)
            val numAfterTenThousand: String = numStr[numStr.length - 4].toString()
            return if (numAfterTenThousand != "0") {
                "$tenThousand.$numAfterTenThousand" + "万"
            } else tenThousand + "万"
        }
        if (num > 100000000) {
            /*val numStr = num.toString()
            val yi = numStr.substring(0, numStr.length - 8)
            val numAfterYi : String = numStr[numStr.length - 8].toString()
            return if(numAfterYi != "0"){
                "$yi.$numAfterYi" + "亿"
            } else yi + "亿"*/
            return num.toString()
        }
        return ""
    }

    fun num2Chinese(num: Int): String {
        if (num <= 0) {
            return "0"
        }
        if (num < 10000) {
            return num.toString()
        }
        if (num in 10000..99999999) {
            val numStr = num.toString()
            val tenThousand = numStr.substring(0, numStr.length - 4)
            val numAfterTenThousand: String = numStr[numStr.length - 4].toString()
            return if (numAfterTenThousand != "0") {
                "$tenThousand.$numAfterTenThousand" + "万"
            } else tenThousand + "万"
        }
        return ""
    }
}