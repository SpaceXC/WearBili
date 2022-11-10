package cn.spacexc.wearbili.activity.user

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.adapter.StarFolderAdapter
import cn.spacexc.wearbili.databinding.ActivityStaredBinding
import cn.spacexc.wearbili.dataclass.star.StarFolderData
import cn.spacexc.wearbili.dataclass.star.StarList
import cn.spacexc.wearbili.manager.UserManager
import cn.spacexc.wearbili.utils.NetworkUtils
import cn.spacexc.wearbili.utils.TimeUtils
import cn.spacexc.wearbili.utils.ViewUtils.addClickScale
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class StaredActivity : AppCompatActivity() {
    lateinit var binding: ActivityStaredBinding
    val adapter = StarFolderAdapter(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaredBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        binding.pageName.setOnClickListener { finish() }
        binding.swipeRefreshLayout.setOnRefreshListener { getStarFolder() }
//        lifecycleScope.launchWhenCreated {
//            while (true) {
//                binding.timeText.text = TimeUtils.getCurrentTime()
//                delay(500)
//            }
//        }
        binding.swipeRefreshLayout.isRefreshing = true
        binding.cardView.addClickScale()
        getStarFolder()
    }

    fun getStarFolder() {
        UserManager.getStarFolderList(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                NetworkUtils.requireRetry {
                    getStarFolder()
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call, response: Response) {
                val result = Gson().fromJson(response.body?.string(), StarList::class.java)
                val tempList = result.data.list.toMutableList()
                if (result.code == 0) {
                    result.data.list[0].also { mainFolder ->
                        UserManager.getStarFolderData(mainFolder.id, object : Callback {
                            override fun onFailure(call: Call, e: IOException) {
                                NetworkUtils.requireRetry {

                                }
                            }

                            override fun onResponse(call: Call, response: Response) {
                                val main = Gson().fromJson(
                                    response.body?.string(),
                                    StarFolderData::class.java
                                )
                                MainScope().launch {
                                    Glide.with(this@StaredActivity).load(main.data.cover)
                                        .placeholder(
                                            R.drawable.placeholder
                                        ).into(binding.cover)
                                    binding.swipeRefreshLayout.isRefreshing = false
                                }
                            }
                        })
                    }
                    MainScope().launch {
                        binding.itemCount.text = "${result.data.list[0].media_count}/50000"
                        binding.title.text = result.data.list[0].title
                        binding.cardView.setOnClickListener {
                            val intent = Intent(this@StaredActivity, StarItemActivity::class.java)
                            intent.putExtra("folderId", result.data.list[0].id)
                            startActivity(intent)
                        }
                        tempList.removeFirst()
                        adapter.submitList(tempList)
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                }
            }

        })
    }
}