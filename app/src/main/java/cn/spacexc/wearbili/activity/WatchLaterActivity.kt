package cn.spacexc.wearbili.activity

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.adapter.WatchLaterAdapter
import cn.spacexc.wearbili.dataclass.watchlater.WatchLater
import cn.spacexc.wearbili.utils.TimeUtils
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
    }

    fun getWatchLater() {
        if (cn.spacexc.wearbili.manager.UserManager.isLoggedIn()) {
            cn.spacexc.wearbili.manager.UserManager.getWatchLater(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    mThreadPool.execute {
                        this@WatchLaterActivity.runOnUiThread {
                            swipeRefreshLayout.isRefreshing = false
                            Toast.makeText(this@WatchLaterActivity, "网络异常", Toast.LENGTH_SHORT)
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
            Toast.makeText(this, "你还没有登录哦", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("fromHome", false)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            return
        }
    }

    override fun onResume() {
        super.onResume()
        swipeRefreshLayout.isRefreshing = true
        getWatchLater()
    }
}