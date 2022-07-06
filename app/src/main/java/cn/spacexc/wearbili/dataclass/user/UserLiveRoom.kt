package cn.spacexc.wearbili.dataclass.user

data class UserLiveRoom(
    val broadcast_type: Int,
    val cover: String,
    val liveStatus: Int,
    val roomStatus: Int,
    val roomid: Int,
    val roundStatus: Int,
    val title: String,
    val url: String,
    val watched_show: UserWatchedShow
)