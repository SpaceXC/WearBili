package cn.spacexc.wearbili.activity

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.adapter.WatchLaterAdapter
import cn.spacexc.wearbili.dataclass.watchlater.WatchLater
import cn.spacexc.wearbili.manager.UserManager
import cn.spacexc.wearbili.utils.TimeUtils
import cn.spacexc.wearbili.utils.ToastUtils
import com.google.gson.Gson
import kotlinx.coroutines.delay
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class WatchLaterActivity : AppCompatActivity() {
    val mThreadPool: ExecutorService = Executors.newCachedThreadPool()
    val adapter = WatchLaterAdapter(this)
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var pageName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watch_later)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        pageName = findViewById(R.id.pageName)
        val timeText = findViewById<TextView>(R.id.timeText)
        pageName.text = "稍后再看"// (${data?.data?.size})"
        pageName.setOnClickListener { finish() }
        lifecycleScope.launchWhenCreated {
            while (true) {
                timeText.text = TimeUtils.getCurrentTime()
                delay(500)
            }
        }
        swipeRefreshLayout.setOnRefreshListener { getWatchLater() }
        swipeRefreshLayout.isRefreshing = true
        getWatchLater()
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val video = adapter.currentList[viewHolder.absoluteAdapterPosition]
                UserManager.deleteVideoFromWatchLater(video.aid, object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        mThreadPool.execute {
                            this@WatchLaterActivity.runOnUiThread {
                                ToastUtils.makeText(
                                    this@WatchLaterActivity,
                                    "网络异常",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        mThreadPool.execute {
                            this@WatchLaterActivity.runOnUiThread {
                                ToastUtils.makeText(
                                    this@WatchLaterActivity,
                                    "删除成功",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                swipeRefreshLayout.isRefreshing = true
                                getWatchLater()
                            }
                        }
                    }

                })
            }

        }).attachToRecyclerView(recyclerView)
    }

    private fun getWatchLater() {
        if (UserManager.isLoggedIn()) {
            UserManager.getWatchLater(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    mThreadPool.execute {
                        this@WatchLaterActivity.runOnUiThread {
                            swipeRefreshLayout.isRefreshing = false
                            ToastUtils.makeText(this@WatchLaterActivity, "网络异常", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val result = Gson().fromJson(response.body?.string(), WatchLater::class.java)
                    mThreadPool.execute {
                        this@WatchLaterActivity.runOnUiThread {
                            swipeRefreshLayout.isRefreshing = false
                            adapter.submitList(result.data.list)
                            pageName.text = "稍后再看(${result.data.count})"
                        }
                    }
                }
            })
        } else {
            ToastUtils.makeText(this, "你还没有登录哦", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("fromHome", false)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            return
        }
    }
}