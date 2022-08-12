package cn.spacexc.wearbili.dataclass.follow

data class FollowGroupUserItem(
    val attribute: Int,
    val contract_info: ContractInfo,
    val face: String,
    val face_nft: Int,
    val live: Live,
    val mid: Long,
    val nft_icon: String,
    val official_verify: OfficialVerify,
    val sign: String,
    val special: Int,
    val tag: Any?,
    val uname: String,
    val vip: Vip
)