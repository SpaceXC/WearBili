package cn.spacexc.wearbili.utils

import okio.ArrayIndexOutOfBoundsException

/**
 * Created by XC-Qan on 2022/6/27.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

object VideoUtils {
    //这里是由知乎大佬不知道用什么方法得出的转换用数字
    var ss = intArrayOf(11, 10, 3, 8, 4, 6, 2, 9, 5, 7)
    var xor: Long = 177451812 //二进制时加减数1

    var add = 8728348608L //十进制时加减数2

    //变量初始化工作，加载哈希表
    private const val table = "fZodR9XQDSUm21yCkr6zBqiveYah8bt4xsWpHnJE7jL5VG3guMTKNPAwcF"
    private val mp = HashMap<String, Int>()
    private val mp2 = HashMap<Int, String>()

    //现在，定义av号和bv号互转的方法
//定义一个power乘方方法，这是转换进制必要的
    fun power(a: Int, b: Int): Long {
        var power: Long = 1
        for (c in 0 until b) power *= a.toLong()
        return power
    }

    //bv转av方法
    fun bv2av(s: String): String {
        var r: Long = 0
        //58进制转换
        for (i in 0..57) {
            val s1 = table.substring(i, i + 1)
            mp[s1] = i
        }
        for (i in 0..5) {
            r += mp[s.substring(ss[i], ss[i] + 1)]!! * power(58, i)
        }
        //转换完成后，需要处理，带上两个随机数
        return (r - add xor xor).toString()
    }

    //av转bv方法
    fun av2bv(st: String): String {
        try {
            var s = java.lang.Long.valueOf(st.split("av".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()[1])
            val sb = StringBuffer("BV1  4 1 7  ")
            //逆向思路，先将随机数还原
            s = (s xor xor) + add
            //58进制转回
            for (i in 0..57) {
                val s1 = table.substring(i, i + 1)
                mp2[i] = s1
            }
            for (i in 0..5) {
                val r = mp2[(s / power(58, i) % 58).toInt()]
                sb.replace(ss[i], ss[i] + 1, r!!)
            }
            return sb.toString()
        } catch (e: ArrayIndexOutOfBoundsException) {
            return ""
        }
    }

}

