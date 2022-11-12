package cn.spacexc.wearbili.dataclass.video.rcmd

data class Args(
    val aid: Int,
    val rid: Int,
    val rname: String,
    val tid: Int?,
    val tname: String?,
    val up_id: Long,
    val up_name: String?
)