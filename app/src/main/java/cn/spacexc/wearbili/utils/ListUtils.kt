package cn.spacexc.wearbili.utils

/**
 * Created by XC-Qan on 2022/7/23.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

/*fun main() {
    val list1 = mutableListOf(
        1,
        2,
        3,
        4,
        5
    )
    val list2 = listOf(
        1,
        2,
        5,
        9,
        10
    )

    list1.addListToAnother(list2)
    println(list1)
}*/

object ListUtils {
    @Deprecated("这个傻逼方法卵用没有", ReplaceWith("CookiesManager.unionCookies()"), DeprecationLevel.WARNING)
    fun <T> MutableList<T>.addListToAnother(secondList: List<T>) {
        for (node in this) {
            for (node2 in secondList) {
                if (node != node2) {
                    this.add(node2)
                }
            }
        }
    }
}