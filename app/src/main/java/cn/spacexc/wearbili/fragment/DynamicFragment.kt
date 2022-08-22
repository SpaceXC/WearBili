package cn.spacexc.wearbili.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.widget.WearableLinearLayoutManager
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.activity.user.LoginActivity
import cn.spacexc.wearbili.adapter.DynamicAdapter
import cn.spacexc.wearbili.databinding.FragmentDynamicBinding
import cn.spacexc.wearbili.dataclass.dynamic.Card
import cn.spacexc.wearbili.manager.DynamicManager
import cn.spacexc.wearbili.utils.RecyclerViewUtils.TopLinearSmoothScroller
import cn.spacexc.wearbili.utils.ToastUtils
import cn.spacexc.wearbili.utils.ToastUtils.debugToast
import cn.spacexc.wearbili.utils.WearableLayoutManagerCallback
import com.bumptech.glide.Glide
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.Call


class DynamicFragment : Fragment() {
    private var _binding: FragmentDynamicBinding? = null
    val binding get() = _binding!!


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

    fun refresh() {
        if (isAdded) {
            requireActivity().runOnUiThread {
                binding.recyclerView.smoothScrollToPosition(0)
                binding.swipeRefreshLayout.isRefreshing = true
                getDynamic()
            }
        }


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.layoutManager = object :
            WearableLinearLayoutManager(requireActivity(), WearableLayoutManagerCallback()) {
            override fun smoothScrollToPosition(
                recyclerView: RecyclerView?,
                state: RecyclerView.State?,
                position: Int
            ) {
                val scroller = TopLinearSmoothScroller(view.context)
                scroller.targetPosition = position
                startSmoothScroll(scroller)
            }
        }
        adapter = DynamicAdapter(requireContext())
        binding.recyclerView.adapter = adapter
        binding.swipeRefreshLayout.setOnRefreshListener {
            getDynamic()
        }
        binding.login.setOnClickListener {
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.putExtra("fromHome", false)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
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

    fun getDynamic() {
        ToastUtils.debugToast("检测登录...")
        if (!cn.spacexc.wearbili.manager.UserManager.isLoggedIn()) {
            binding.swipeRefreshLayout.isRefreshing = false
            binding.swipeRefreshLayout.visibility = View.GONE
            binding.requireLogin.visibility = View.VISIBLE

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

            override fun onSuccess(dynamicCards: List<Card>, code: Int) {
                MainScope().launch {
                    when (code) {
                        0 -> {
                            binding.login.visibility = View.GONE
                            binding.requireLogin.visibility = View.GONE
                            binding.swipeRefreshLayout.isRefreshing = false
                            binding.swipeRefreshLayout.visibility = View.VISIBLE
                            //ToastUtils.makeText("动态获取成功$dynamicCards").show()
                            adapter.submitList(dynamicCards.toMutableList())
                            binding.recyclerView.smoothScrollToPosition(0)
                        }
                        -6 -> {
                            binding.requireLogin.visibility = View.VISIBLE
                            binding.swipeRefreshLayout.visibility = View.GONE
                        }
                    }

                }

            }

        })
    }

    private fun getMoreDynamic() {
        //if (!isAdded) return
        if (!cn.spacexc.wearbili.manager.UserManager.isLoggedIn()) {
            binding.swipeRefreshLayout.isRefreshing = false
            binding.swipeRefreshLayout.visibility = View.GONE
            binding.requireLogin.visibility = View.VISIBLE
        }
        DynamicManager.getMoreDynamic(object : DynamicManager.DynamicResponseCallback {
            override fun onFailed(call: Call, e: Exception) {

                MainScope().launch {
                    //binding.swipeRefreshLayout.isRefreshing = false
                    ToastUtils.makeText("动态获取失败").show()
                }

            }

            override fun onSuccess(dynamicCards: List<Card>, code: Int) {
                MainScope().launch {
                    when (code) {
                        0 -> {
                            binding.requireLogin.visibility = View.GONE
                            binding.requireLogin.visibility = View.GONE
                            binding.swipeRefreshLayout.visibility = View.VISIBLE
                            adapter.submitList(adapter.currentList + dynamicCards.toMutableList())
                        }
                        -6 -> {
                            binding.requireLogin.visibility = View.VISIBLE
                            binding.swipeRefreshLayout.visibility = View.GONE
                        }
                    }
                }

            }

        })
    }
}