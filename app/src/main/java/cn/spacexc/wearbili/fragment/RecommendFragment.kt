package cn.spacexc.wearbili.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.widget.WearableLinearLayoutManager
import cn.spacexc.wearbili.Application.Companion.TAG
import cn.spacexc.wearbili.adapter.VideoPhoneEndRecommendListAdapter
import cn.spacexc.wearbili.databinding.FragmentRecommendBinding
import cn.spacexc.wearbili.dataclass.video.rcmd.Item
import cn.spacexc.wearbili.dataclass.video.rcmd.RecommendVideo
import cn.spacexc.wearbili.manager.VideoManager
import cn.spacexc.wearbili.utils.ToastUtils
import cn.spacexc.wearbili.utils.WearableLayoutManagerCallback
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException


class RecommendFragment : Fragment() {
    private var _binding: FragmentRecommendBinding? = null
    val binding get() = _binding!!

    lateinit var adapter: VideoPhoneEndRecommendListAdapter


    var videoList: Array<Item> = emptyArray()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecommendBinding.inflate(inflater, container, false)
        return binding.root
    }

    //TODO : Memory Leak
    /*override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: $tag")
        adapter = VideoPhoneEndRecommendListAdapter(requireContext())
        binding.recyclerView.layoutManager =
            WearableLinearLayoutManager(requireContext(), WearableLayoutManagerCallback())
        binding.recyclerView.adapter = adapter
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
                    getRecommendVideo(false)
                }
            }
        })
        binding.swipeRefreshLayout.setOnRefreshListener {
            videoList = emptyArray()
            getRecommendVideo(true)
        }

    }

    override fun onResume() {
        super.onResume()
        if (adapter.currentList.isEmpty()) {
            binding.swipeRefreshLayout.isRefreshing = true
            getRecommendVideo(false)
        }
    }

    fun refresh() {
        if (isAdded) {
            requireActivity().runOnUiThread {
                this.binding.recyclerView.smoothScrollToPosition(0)
                this.binding.swipeRefreshLayout.isRefreshing = true
                videoList = emptyArray()
                getRecommendVideo(true)
            }
        }


    }

    fun getRecommendVideo(isRefresh: Boolean) {
        lifecycleScope.launch {
            kotlin.runCatching {
                VideoManager.getRecommendVideo(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        MainScope().launch {
                            binding.swipeRefreshLayout.isRefreshing = false


                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val str = response.body?.string()
                        val videos = Gson().fromJson(str, RecommendVideo::class.java)
                        videoList += videos.data.items
                        MainScope().launch {
                            if (response.code == 200) {
                                binding.swipeRefreshLayout.isRefreshing = false
                                adapter.submitList(videoList.toList())
                                if (isRefresh) ToastUtils.makeText(
                                    "小电视推荐了一批新内容"
                                ).show()
                            } else {
                                ToastUtils.makeText(
                                    "加载失败"
                                ).show()
                                binding.swipeRefreshLayout.isRefreshing = false

                            }

                        }


                    }

                })
            }

        }

    }
}