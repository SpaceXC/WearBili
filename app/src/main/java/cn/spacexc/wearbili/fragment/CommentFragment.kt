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
import cn.spacexc.wearbili.activity.VideoActivity
import cn.spacexc.wearbili.adapter.CommentAdapter
import cn.spacexc.wearbili.databinding.FragmentCommentBinding
import cn.spacexc.wearbili.dataclass.CommentContentData
import cn.spacexc.wearbili.dataclass.VideoComment
import cn.spacexc.wearbili.manager.VideoManager
import cn.spacexc.wearbili.utils.ToastUtils
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CommentFragment : Fragment() {

    private var _binding: FragmentCommentBinding? = null
    private val binding get() = _binding!!


    val mThreadPool: ExecutorService = Executors.newCachedThreadPool()

    var page : Int = 1

    val adapter = CommentAdapter(lifecycleScope, Application.context!!)
    private val layoutManager = LinearLayoutManager(Application.getContext())

    var prevList : MutableList<CommentContentData>? = null


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isAdded) return
        binding.recyclerView.adapter = adapter.also {
            adapter.uploaderMid = (activity as VideoActivity).currentVideo?.owner?.mid
        }
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                val lm = recyclerView.layoutManager as LinearLayoutManager?
                val totalItemCount = recyclerView.adapter!!.itemCount
                val lastVisibleItemPosition = lm!!.findLastVisibleItemPosition()
                val visibleItemCount = recyclerView.childCount
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemPosition == totalItemCount - 1 && visibleItemCount > 0) {
                    getComment()
                }
            }
        })
        binding.swipeRefreshLayout.setOnRefreshListener {
            getComment()
        }
        binding.swipeRefreshLayout.isRefreshing = true
        getComment()
    }

    fun getComment() {
        if ((activity as VideoActivity).currentVideo != null) {
            VideoManager.getCommentsByLikes(
                (activity as VideoActivity).currentVideo!!.aid,
                page,
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        if (isAdded) {
                            mThreadPool.execute {
                                requireActivity().runOnUiThread {
                                    binding.swipeRefreshLayout.isRefreshing = false
                                    ToastUtils.makeText(
                                        "评论加载失败啦"
                                    )
                                        .show()
                                }
                            }
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val comments =
                            Gson().fromJson(response.body?.string(), VideoComment::class.java)
                        if (isAdded) {
                            mThreadPool.execute {
                                (activity as VideoActivity).runOnUiThread {
                                    if (comments.code == 0) {

                                        val replies: MutableList<CommentContentData> =
                                            comments.data.replies?.toMutableList()
                                                ?: mutableListOf()
                                        if (replies != prevList) {
                                            prevList = replies
                                            if (comments.data.top.member != null && comments.data.top.content != null) {
                                                val top = comments.data.top
                                                top.is_top = true
                                                replies.remove(comments.data.top)
                                                val topList = mutableListOf(top)

                                                val finalList = topList + replies
                                                adapter.submitList(finalList)
                                            } else {
                                                adapter.submitList(adapter.currentList + replies)

                                            }
                                            page++
                                            binding.swipeRefreshLayout.isRefreshing = false
                                        } else {
                                            //ToastUtils.makeText(requireContext(), "再怎么翻都没有啦", Toast.LENGTH_SHORT).show()
                                        }


                                        /*if(comments.data.cursor.is_begin){
                                            val replies : MutableList<CommentContentData> = comments.data.replies.toMutableList()

                                            if(comments.data.top_replies != null){
                                                val top = mutableListOf(comments.data.top_replies)
                                                replies.remove(comments.data.top_replies)
                                                adapter.submitList(top + replies)
                                            }
                                            else{
                                                adapter.submitList(replies)

                                            }

            //                                println(adapter.currentList)
                                        }
                                        else{
                                            if(comments.data.cursor.is_end) isEnd = true
                                            adapter.submitList(adapter.currentList + comments.data.replies)
                                        }*/

                                    } else {
                                        binding.swipeRefreshLayout.isRefreshing = false
                                        ToastUtils.makeText(
                                            "评论加载失败啦"
                                        ).show()
                                    }
                                }
                            }
                        }
                    }

                })
        }
    }
}