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
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.activity.video.VideoActivity
import cn.spacexc.wearbili.adapter.CommentAdapter
import cn.spacexc.wearbili.databinding.FragmentCommentBinding
import cn.spacexc.wearbili.dataclass.CommentContentData
import cn.spacexc.wearbili.dataclass.VideoComment
import cn.spacexc.wearbili.manager.VideoManager
import cn.spacexc.wearbili.utils.RecyclerViewUtils
import cn.spacexc.wearbili.utils.ToastUtils
import com.google.gson.Gson
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class CommentFragment : Fragment() {

    private var _binding: FragmentCommentBinding? = null
    private val binding get() = _binding!!


    var page: Int = 1

    val adapter = CommentAdapter(lifecycleScope, Application.context!!)
    //lateinit var layoutManager: WearableLinearLayoutManager

    init {
        Log.d(Application.getTag(), "CommentFragmentLoaded")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun refresh() {
        if (isAdded) {
            requireActivity().runOnUiThread {
                page = 1
                binding.recyclerView.smoothScrollToPosition(0)
                binding.swipeRefreshLayout.isRefreshing = true
                getComment(true)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isAdded) return
        binding.recyclerView.adapter = adapter.also {
            adapter.uploaderMid = (activity as VideoActivity).currentVideo?.owner?.mid
        }
        binding.recyclerView.layoutManager = object : LinearLayoutManager(requireActivity()) {
            override fun smoothScrollToPosition(
                recyclerView: RecyclerView?,
                state: RecyclerView.State?,
                position: Int
            ) {
                val scroller = RecyclerViewUtils.TopLinearSmoothScroller(view.context)
                scroller.targetPosition = position
                startSmoothScroll(scroller)
            }
        }
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                val lm = recyclerView.layoutManager as LinearLayoutManager?
                val totalItemCount = recyclerView.adapter!!.itemCount
                val lastVisibleItemPosition = lm!!.findLastVisibleItemPosition()
                val visibleItemCount = recyclerView.childCount
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemPosition == totalItemCount - 1 && visibleItemCount > 0) {
                    getComment(false)
                }
            }
        })
        binding.swipeRefreshLayout.setOnRefreshListener {
            page = 1
            getComment(true)
        }

    }

    override fun onResume() {
        super.onResume()
        if (adapter.currentList.isEmpty()) {
            binding.swipeRefreshLayout.isRefreshing = true
            getComment(true)
        }
    }

    fun getComment(isRefresh: Boolean) {
        if ((activity as VideoActivity).currentVideo != null) {
            VideoManager.getCommentsByLikes(
                (activity as VideoActivity).currentVideo!!.aid,
                page,
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        if (isAdded) {
                            MainScope().launch {
                                binding.swipeRefreshLayout.isRefreshing = false
                                ToastUtils.makeText(
                                    "评论加载失败啦"
                                )
                                    .show()
                            }

                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val result =
                            Gson().fromJson(response.body?.string(), VideoComment::class.java)
                        if (isAdded) {
                            MainScope().launch {
                                if (result.code == 0) {
                                    if (result.data.cursor.is_end) return@launch
                                    if (isRefresh) {
                                        var top = mutableListOf<CommentContentData>()
                                        if (result.data.top?.content != null && result.data.top.member != null) {
                                            top = mutableListOf(result.data.top)
                                        }
                                        adapter.submitList(top + result.data.replies?.toMutableList()!!)
                                    } else {
                                        adapter.submitList(adapter.currentList + result.data.replies!!)
                                    }
                                    binding.swipeRefreshLayout.isRefreshing = false
                                    page++
                                } else {
                                    binding.swipeRefreshLayout.isRefreshing = false
                                    ToastUtils.makeText(
                                        "评论加载失败啦"
                                    ).show()
                                }
                            }
                        }
                    }

                })
        }
    }
}