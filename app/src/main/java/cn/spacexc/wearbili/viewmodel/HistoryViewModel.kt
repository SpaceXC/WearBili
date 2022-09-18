package cn.spacexc.wearbili.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.spacexc.wearbili.dataclass.history.History
import cn.spacexc.wearbili.dataclass.history.HistoryObject
import cn.spacexc.wearbili.manager.UserManager
import cn.spacexc.wearbili.utils.NetworkUtils
import cn.spacexc.wearbili.utils.ToastUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * Created by Xiaochang on 2022/9/17.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */
class HistoryViewModel : ViewModel() {
    private val _historyList = MutableLiveData<List<HistoryObject>>()
    val historyList: LiveData<List<HistoryObject>> = _historyList
    var viewAtTimeStamp = 0L
    var isRefreshing = MutableLiveData(false)

    init {
        getHistory(true)
    }

    fun getHistory(isRefresh: Boolean = false) {
        isRefreshing.value = true
        if (isRefresh) viewAtTimeStamp = 0L
        UserManager.getHistory(viewAtTimeStamp, object : NetworkUtils.ResultCallback<History> {
            override fun onSuccess(result: History, code: Int) {
                MainScope().launch {
                    if (result.code == 0) {
                        if (isRefresh) _historyList.value = result.data.list
                        else _historyList.value = _historyList.value?.plus(result.data.list)
                        viewAtTimeStamp = result.data.list.last().view_at
                        isRefreshing.value = false
                    }
                    else{
                        ToastUtils.showText("${result.code}: ${result.message}")
                    }
                }
            }

            override fun onFailed(e: Exception) {
                MainScope().launch {
                    isRefreshing.value = false

                }
            }

        })
    }
}