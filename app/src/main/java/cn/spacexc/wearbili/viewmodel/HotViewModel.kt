package cn.spacexc.wearbili.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.spacexc.wearbili.dataclass.hot.rankinglist.RankingList
import cn.spacexc.wearbili.manager.HotManager
import cn.spacexc.wearbili.utils.NetworkUtils
import cn.spacexc.wearbili.utils.ToastUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/*
 * Created by XC on 2022/10/30.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class HotViewModel : ViewModel() {
    private val _rankingList = MutableLiveData<RankingList>()
    val rankingList: LiveData<RankingList> = _rankingList
    fun getRankingList() {
        HotManager.getRankingList(object : NetworkUtils.ResultCallback<RankingList> {
            override fun onSuccess(result: RankingList, code: Int) {
                MainScope().launch {
                    _rankingList.value = result
                }
            }

            override fun onFailed(e: Exception) {
                MainScope().launch {
                    ToastUtils.showText("网络异常")
                }
            }

        })
    }
}