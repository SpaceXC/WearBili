package cn.spacexc.wearbili.dataclass

import androidx.room.Entity
import androidx.room.PrimaryKey

/* 
WearBili Copyright (C) 2022 XC
This program comes with ABSOLUTELY NO WARRANTY.
This is free software, and you are welcome to redistribute it under certain conditions.
*/

/*
 * Created by XC on 2022/12/7.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

@Entity
data class DownloadItemVideoInfo(
    val timeStamp: Long,
    val mediaId: String,
    val uri: String,
    val customCacheKey: String?,
    val mimeType: String?,
    //val streamKeys: List<StreamKey>,
    @PrimaryKey val cid: String,
    val stat: Int,
)
