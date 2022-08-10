package cn.spacexc.wearbili.activity.video

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.spacexc.wearbili.databinding.ActivityVideoLongClickBinding
import cn.spacexc.wearbili.dataclass.SimplestUniversalDataClass
import cn.spacexc.wearbili.manager.VideoManager
import cn.spacexc.wearbili.utils.ToastUtils
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class VideoLongClickActivity : AppCompatActivity() {
    val mThreadPool: ExecutorService = Executors.newCachedThreadPool()

    private lateinit var binding: ActivityVideoLongClickBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoLongClickBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.goBack.setOnClickListener { finish() }

        binding.addToWatchLater.setOnClickListener {
            intent.getStringExtra("bvid")?.let { it1 ->
                VideoManager.addToView(it1, object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        mThreadPool.execute {
                            this@VideoLongClickActivity.runOnUiThread {
                                ToastUtils.makeText(
                                    "网络异常"
                                ).show()
                                finish()
                            }
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val result = Gson().fromJson(
                            response.body?.string(),
                            SimplestUniversalDataClass::class.java
                        )
                        mThreadPool.execute {
                            this@VideoLongClickActivity.runOnUiThread {
                                when (result.code) {
                                    0 -> {
                                        ToastUtils.makeText(
                                            "添加成功"
                                        ).show()
                                        finish()
                                    }
                                    90001 -> {
                                        ToastUtils.makeText(
                                            "稍后再看列表已满"
                                        ).show()
                                        finish()
                                    }
                                    90003 -> {
                                        ToastUtils.makeText(
                                            "视频不见了"
                                        ).show()
                                        finish()
                                    }
                                }
                            }
                        }
                    }

                })
            }
        }
    }
}