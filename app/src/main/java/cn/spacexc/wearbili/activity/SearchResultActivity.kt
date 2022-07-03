package cn.spacexc.wearbili.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.adapter.SearchResultAdapter
import cn.spacexc.wearbili.databinding.ActivitySearchResultBinding
import cn.spacexc.wearbili.dataclass.VideoSearch
import cn.spacexc.wearbili.manager.VideoManager
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SearchResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchResultBinding
    val mThreadPool: ExecutorService = Executors.newCachedThreadPool()

    val adapter : SearchResultAdapter = SearchResultAdapter()
    private val layoutManager = LinearLayoutManager(this)

    var currentPage : Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = layoutManager
        binding.swipeRefreshLayout.isRefreshing = true
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                val lm = recyclerView.layoutManager as LinearLayoutManager?
                val totalItemCount = recyclerView.adapter!!.itemCount
                val lastVisibleItemPosition = lm!!.findLastVisibleItemPosition()
                val visibleItemCount = recyclerView.childCount
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemPosition == totalItemCount - 1 && visibleItemCount > 0) {
                    searchVideo()
                }
            }
        })
        binding.pageName.setOnClickListener { finish() }
        lifecycleScope.launch {
            @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat("HH:mm")
            while (true) {
                val date = sdf.format(Date())
                binding.timeText.text = date
                delay(500)
            }
        }
        searchVideo()
    }

    private fun searchVideo(){
        Log.d(Application.getTag(), "searchVideo: $currentPage")
        val keyword = intent.getStringExtra("keyword")

        VideoManager.searchVideo(keyword!!, currentPage, object : Callback{


            override fun onFailure(call: Call, e: IOException) {
                mThreadPool.execute {
                    this@SearchResultActivity.runOnUiThread {
                        Toast.makeText(this@SearchResultActivity, "搜索失败了", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val result = Gson().fromJson(response.body?.string(), VideoSearch::class.java)
                mThreadPool.execute {
                    this@SearchResultActivity.runOnUiThread {
                        binding.pageName.text = "搜索结果 (${result.data.numResults})"
                        if (result.code == 0) {
                            if (currentPage <= 50) {
                                adapter.submitList(adapter.currentList + result.data.result!!)
                                binding.swipeRefreshLayout.isRefreshing = false
                                currentPage++

                            } else {
                                binding.swipeRefreshLayout.isRefreshing = false
                                Toast.makeText(
                                    this@SearchResultActivity,
                                    "搜索到底了",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                        } else {
                            binding.swipeRefreshLayout.isRefreshing = false
                            Toast.makeText(this@SearchResultActivity, "搜索失败了", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }

            }


        })
    }
}