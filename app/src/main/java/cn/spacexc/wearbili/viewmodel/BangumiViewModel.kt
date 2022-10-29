package cn.spacexc.wearbili.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.spacexc.wearbili.dataclass.bangumi.BangumiDetail
import cn.spacexc.wearbili.manager.BangumiManager
import cn.spacexc.wearbili.manager.ID_TYPE_EPID
import cn.spacexc.wearbili.utils.NetworkUtils
import cn.spacexc.wearbili.utils.ToastUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/*
 * Created by XC on 2022/10/28.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class BangumiViewModel : ViewModel() {
    private val _bangumi: MutableLiveData<BangumiDetail> = MutableLiveData()
    val bangumi: LiveData<BangumiDetail> = _bangumi

    fun getBangumi(id: String, idType: String = ID_TYPE_EPID) {
        BangumiManager.getBangumiDetail(
            id,
            idType,
            object : NetworkUtils.ResultCallback<BangumiDetail> {
                override fun onSuccess(result: BangumiDetail, code: Int) {
                    MainScope().launch {
                        _bangumi.value = result
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