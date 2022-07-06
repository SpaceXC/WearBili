package cn.spacexc.wearbili.dataclass

/**
 * Created by XC-Qan on 2022/7/5.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

data class UserFans(
    val data: UserFansData
) {
    data class UserFansData(
        val card: FansCard
    )

    data class FansCard(
        val fans: Long
    )
}