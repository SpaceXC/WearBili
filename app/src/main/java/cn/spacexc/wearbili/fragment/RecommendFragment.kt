package cn.spacexc.wearbili.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.spacexc.wearbili.adapter.VideoRecommendListAdapter
import cn.spacexc.wearbili.databinding.FragmentRecommendBinding
import cn.spacexc.wearbili.dataclass.VideoRecommend
import cn.spacexc.wearbili.dataclass.VideoRecommendItem
import cn.spacexc.wearbili.manager.VideoManager
import cn.spacexc.wearbili.utils.ToastUtils
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
    private val binding get() = _binding!!

    lateinit var adapter: VideoRecommendListAdapter


    var videoList: Array<VideoRecommendItem> = emptyArray()

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
        adapter = VideoRecommendListAdapter(requireContext())
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
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
        binding.swipeRefreshLayout.isRefreshing = true
        getRecommendVideo(false)
    }

    private fun getRecommendVideo(isRefresh: Boolean) {
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
                        val videos = Gson().fromJson(str, VideoRecommend::class.java)
                        videoList += videos.data.item
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