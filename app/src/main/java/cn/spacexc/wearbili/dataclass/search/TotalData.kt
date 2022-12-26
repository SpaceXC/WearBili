package cn.spacexc.wearbili.dataclass.search

import cn.spacexc.wearbili.dataclass.search.mediaft.SearchedMediaFt
import cn.spacexc.wearbili.dataclass.search.user.SearchedUser
import cn.spacexc.wearbili.dataclass.search.video.SearchedVideo

/* 
WearBili Copyright (C) 2022 XC
This program comes with ABSOLUTELY NO WARRANTY.
This is free software, and you are welcome to redistribute it under certain conditions.
*/

/*
 * Created by XC on 2022/12/25.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

data class TotalData(
    val users: List<SearchedUser>,
    val medias: List<SearchedMediaFt>,
    val videos: List<SearchedVideo>
)