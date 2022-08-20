package cn.spacexc.wearbili.utils

/**
 * Created by XC-Qan on 2022/7/23.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

/*fun main() {
    val list1 = mutableListOf<Int>(
        1,
        2,
        3,
        4,
        5
    )
    val list2 = mutableListOf(
        1,
        2,
        5,
        9,
        10
    )

    list1.add(list2)
    println(list1)
}*/

object ListUtils {
    @Deprecated("这个傻逼方法已经过时了", ReplaceWith("CookiesManager.unionCookies()"), DeprecationLevel.ERROR)
    fun <T : Any> MutableList<T>.add(secondList: List<T>) {
        for (node in this) {
            for (node2 in secondList) {
                if (node != node2) {
                    this.add(node2)
                }
            }
        }
    }
}