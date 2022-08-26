package cn.spacexc.wearbili.activity.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.widget.WearableLinearLayoutManager
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.adapter.SearchResultAdapter
import cn.spacexc.wearbili.databinding.ActivitySearchResultBinding
import cn.spacexc.wearbili.dataclass.VideoSearch
import cn.spacexc.wearbili.manager.VideoManager
import cn.spacexc.wearbili.utils.LogUtils.log
import cn.spacexc.wearbili.utils.ToastUtils
import cn.spacexc.wearbili.utils.WearableLayoutManagerCallback
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.util.*

class SearchResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchResultBinding


    lateinit var adapter: SearchResultAdapter
    private val layoutManager = WearableLinearLayoutManager(this, WearableLayoutManagerCallback())

    var currentPage: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = SearchResultAdapter(this)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = layoutManager
        binding.swipeRefreshLayout.isRefreshing = true
        binding.pageName.text = getKeyword()
        binding.pageName.requestFocus()
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING or RecyclerView.SCROLL_STATE_SETTLING) {
                    Glide.with(this@SearchResultActivity).pauseRequests()
                } else {
                    Glide.with(this@SearchResultActivity).resumeRequests()
                }
                val lm = recyclerView.layoutManager as LinearLayoutManager?
                val totalItemCount = recyclerView.adapter!!.itemCount
                val lastVisibleItemPosition = lm!!.findLastVisibleItemPosition()
                val visibleItemCount = recyclerView.childCount
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemPosition == totalItemCount - 1 && visibleItemCount > 0) {
                    searchVideo()
                }
            }
        })
        binding.swipeRefreshLayout.setOnRefreshListener {
            searchVideo()
        }
        binding.pageName.setOnClickListener { finish() }
        lifecycleScope.launch {
            while (true) {
                binding.timeText.text = cn.spacexc.wearbili.utils.TimeUtils.getCurrentTime()
                delay(500)
            }
        }
        searchVideo()
    }

    private fun searchVideo() {
        Log.d(Application.getTag(), "searchVideo: $currentPage")
        val keyword = getKeyword()

        VideoManager.searchVideo(keyword ?: "", currentPage, object : Callback {


            override fun onFailure(call: Call, e: IOException) {
                MainScope().launch {
                    ToastUtils.makeText("搜索失败了")
                        .show()

                }
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call, response: Response) {
                val responseStr = response.body?.string()
                val result = Gson().fromJson(responseStr, VideoSearch::class.java)
                MainScope().launch {
                    responseStr?.log("搜索返回结果")
                    binding.pageName.text = "$keyword (${result.data?.numResults})"
                    if (result.code == 0) {
                        if(result.data?.result != null){
                            binding.noResult.isVisible = false
                            if (currentPage <= 50) {
                                adapter.submitList(adapter.currentList + result.data?.result!!)
                                binding.swipeRefreshLayout.isRefreshing = false
                                currentPage++

                            } else {
                                binding.swipeRefreshLayout.isRefreshing = false
                                ToastUtils.makeText(
                                    "搜索到底了"
                                ).show()

                            }
                        }
                        else{
                            binding.noResult.isVisible = true
                            binding.swipeRefreshLayout.isRefreshing = false
                        }

                    } else {
                        binding.swipeRefreshLayout.isRefreshing = false
                        ToastUtils.makeText(
                            "搜索失败了"
                        ).show()
                    }
                }


            }


        })
    }

    private fun getKeyword(): String? {
        return if (intent.getStringExtra("keyword").isNullOrEmpty()) {
            intent.data?.getQueryParameter("keyword")
        } else {
            intent.getStringExtra("keyword")
        }
    }
}