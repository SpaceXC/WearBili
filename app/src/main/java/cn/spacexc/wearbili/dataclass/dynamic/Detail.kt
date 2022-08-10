package cn.spacexc.wearbili.dataclass.dynamic

/**
 * Created by XC-Qan on 2022/8/4.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

data class Detail(
    val code: Int,
    val msg: String,
    val message: String,
    val data: DetailData

)

data class DetailData(
    val card: Card
)