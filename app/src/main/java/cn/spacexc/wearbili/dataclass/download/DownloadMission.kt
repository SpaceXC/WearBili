package cn.spacexc.wearbili.dataclass.download

/**
 * Created by XC-Qan on 2022/7/13.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

data class DownloadMission(
    val type: MissionType,
    val videoTitle: String,
    val bvid: String,
    val resolution: String,
    val duration: Long,
    val coverUrl: String,
    val networkUrl: String,
    val localUrl: String
)

enum class MissionType {
    PENDING,
    DOWNLOADING,
    COMPLETE,
    FAILED
}