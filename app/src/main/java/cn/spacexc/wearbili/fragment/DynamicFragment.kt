package cn.spacexc.wearbili.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.Application.Companion.TAG
import cn.spacexc.wearbili.activity.MainActivity
import cn.spacexc.wearbili.databinding.FragmentDynamicBinding
import cn.spacexc.wearbili.manager.DynamicManager
import cn.spacexc.wearbili.utils.ToastUtils
import okhttp3.Call
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class DynamicFragment : Fragment() {
    private var _binding: FragmentDynamicBinding? = null
    private val binding get() = _binding!!


    val mThreadPool: ExecutorService = Executors.newCachedThreadPool()

    init {
        Log.d(Application.getTag(), "DynamicFragmentLoaded")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDynamicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDynamic()
    }

    private fun getDynamic() {
        if (!isAdded) return
        if (!cn.spacexc.wearbili.manager.UserManager.isLoggedIn()) {
            ToastUtils.makeText("还没登录呐").show()
            MainActivity.currentPageId.value = 2
        }
        DynamicManager.getRecommendDynamics(object : DynamicManager.DynamicResponseCallback {
            override fun onFailed(call: Call, e: IOException) {
                mThreadPool.execute {
                    requireActivity().runOnUiThread {
                        ToastUtils.makeText("动态获取失败").show()
                    }
                }
            }

            override fun onSuccess(dynamicCards: List<Any?>) {
                mThreadPool.execute {
                    requireActivity().runOnUiThread {
                        ToastUtils.makeText("动态获取成功$dynamicCards").show()
                        Log.d(TAG, "onSuccess: $dynamicCards")
                    }
                }
            }

        })
    }
}