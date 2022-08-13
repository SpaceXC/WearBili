package cn.spacexc.wearbili.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.activity.MainActivity
import cn.spacexc.wearbili.adapter.DynamicAdapter
import cn.spacexc.wearbili.databinding.FragmentDynamicBinding
import cn.spacexc.wearbili.dataclass.dynamic.Card
import cn.spacexc.wearbili.manager.DynamicManager
import cn.spacexc.wearbili.utils.ToastUtils
import cn.spacexc.wearbili.utils.ToastUtils.debugToast
import com.bumptech.glide.Glide
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.Call


class DynamicFragment : Fragment() {
    private var _binding: FragmentDynamicBinding? = null
    private val binding get() = _binding!!


    lateinit var adapter: DynamicAdapter

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
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = DynamicAdapter(requireContext())
        binding.recyclerView.adapter = adapter
        binding.swipeRefreshLayout.setOnRefreshListener {
            getDynamic()
            binding.recyclerView.smoothScrollToPosition(0)

        }
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING or RecyclerView.SCROLL_STATE_SETTLING) {
                    Glide.with(requireContext()).pauseRequests()
                } else {
                    Glide.with(requireContext()).resumeRequests()
                }
                val lm = recyclerView.layoutManager as LinearLayoutManager?
                val totalItemCount = recyclerView.adapter!!.itemCount
                val lastVisibleItemPosition = lm!!.findLastVisibleItemPosition()
                val visibleItemCount = recyclerView.childCount
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemPosition == totalItemCount - 1 && visibleItemCount > 0) {
                    getMoreDynamic()
                }
            }
        })

    }

    override fun onResume() {
        super.onResume()
        if (adapter.currentList.isEmpty()) {
            binding.swipeRefreshLayout.isRefreshing = true
            getDynamic()
        }
    }

    private fun getDynamic() {
        ToastUtils.debugToast("检测登录...")
        if (!cn.spacexc.wearbili.manager.UserManager.isLoggedIn()) {
            ToastUtils.makeText("还没登录呐").show()
            MainActivity.currentPageId.value = 2
        }
        ToastUtils.debugShowText("开始获取动态...")

        DynamicManager.getRecommendDynamics(object : DynamicManager.DynamicResponseCallback {
            override fun onFailed(call: Call, e: Exception) {
                MainScope().launch {
                    binding.swipeRefreshLayout.isRefreshing = false
                    ToastUtils.makeText("动态获取失败").show()
                    e.debugToast("网络异常")
                }

            }

            override fun onSuccess(dynamicCards: List<Card>) {
                MainScope().launch {
                    binding.swipeRefreshLayout.isRefreshing = false
                    //ToastUtils.makeText("动态获取成功$dynamicCards").show()
                    adapter.submitList(dynamicCards.toMutableList())
                }

            }

        })
    }

    private fun getMoreDynamic() {
        //if (!isAdded) return
        if (!cn.spacexc.wearbili.manager.UserManager.isLoggedIn()) {
            ToastUtils.makeText("还没登录呐").show()
            MainActivity.currentPageId.value = 2
        }
        DynamicManager.getMoreDynamic(object : DynamicManager.DynamicResponseCallback {
            override fun onFailed(call: Call, e: Exception) {

                MainScope().launch {
                    //binding.swipeRefreshLayout.isRefreshing = false
                    ToastUtils.makeText("动态获取失败").show()
                }

            }

            override fun onSuccess(dynamicCards: List<Card>) {

                MainScope().launch {
                    //binding.swipeRefreshLayout.isRefreshing = false
                    //ToastUtils.makeText("动态获取成功$dynamicCards").show()
                    adapter.submitList(adapter.currentList + dynamicCards.toMutableList())
                }

            }

        })
    }
}