package cn.spacexc.wearbili.activity.user

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.widget.WearableLinearLayoutManager
import cn.spacexc.wearbili.adapter.StarFolderItemAdapter
import cn.spacexc.wearbili.databinding.ActivityStarItemBinding
import cn.spacexc.wearbili.dataclass.star.StarFolderItemList
import cn.spacexc.wearbili.manager.UserManager
import cn.spacexc.wearbili.utils.NetworkUtils
import cn.spacexc.wearbili.utils.TimeUtils
import cn.spacexc.wearbili.utils.WearableLayoutManagerCallback
import com.google.gson.Gson
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class StarItemActivity : AppCompatActivity() {
    lateinit var binding: ActivityStarItemBinding
    var page = 1
    val adapter = StarFolderItemAdapter(this)
    var hasMore = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStarItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.pageName.setOnClickListener { finish() }
        lifecycleScope.launch {
            while (true) {
                binding.timeText.text = TimeUtils.getCurrentTime()
                delay(500)
            }
        }
        val folderId = intent.getLongExtra("folderId", 0L)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager =
            WearableLinearLayoutManager(this, WearableLayoutManagerCallback())
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                val lm = recyclerView.layoutManager as LinearLayoutManager?
                val totalItemCount = recyclerView.adapter!!.itemCount
                val lastVisibleItemPosition = lm!!.findLastVisibleItemPosition()
                val visibleItemCount = recyclerView.childCount
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemPosition == totalItemCount - 1 && visibleItemCount > 0) {
                    getItems(folderId, false)
                }
            }
        })
        binding.swipeRefreshLayout.setOnRefreshListener {
            page = 1
            getItems(folderId, true)
        }
        binding.swipeRefreshLayout.isRefreshing = true
        getItems(folderId, false)
    }

    fun getItems(id: Long, isRefresh: Boolean) {
        UserManager.getStarFolderItemList(id, page, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                NetworkUtils.requireRetry {
                    getItems(id, false)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val result =
                    Gson().fromJson(response.body?.string(), StarFolderItemList::class.java)
                if (result.code == 0) {
                    MainScope().launch {
                        binding.pageName.text =
                            "${result.data.info.title}(${result.data.info.media_count})"
                        if (isRefresh) {
                            adapter.submitList(result.data.medias)
                        } else {
                            adapter.submitList(adapter.currentList + result.data.medias)
                        }
                        binding.swipeRefreshLayout.isRefreshing = false
                        hasMore = result.data.has_more
                        if (hasMore) {
                            page++
                        }
                    }
                }

            }

        })
    }
}