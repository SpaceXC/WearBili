package cn.spacexc.wearbili.activity.user

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import cn.spacexc.wearbili.adapter.FollowGroupAdapter
import cn.spacexc.wearbili.databinding.ActivityFollowListBinding
import cn.spacexc.wearbili.dataclass.follow.FollowGroup
import cn.spacexc.wearbili.manager.UserManager
import cn.spacexc.wearbili.utils.NetworkUtils
import cn.spacexc.wearbili.utils.TimeUtils
import com.google.gson.Gson
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class FollowListActivity : AppCompatActivity() {
    lateinit var binding: ActivityFollowListBinding
    val adapter = FollowGroupAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFollowListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.pageName.setOnClickListener { finish() }
        lifecycleScope.launch {
            while (true) {
                binding.timeText.text = TimeUtils.getCurrentTime()
                delay(500)
            }
        }
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.pageName.text = adapter.currentList[position].name
            }
        })
        getFollowGroup()
    }

    private fun getFollowGroup() {
        UserManager.getFollowGroups(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                NetworkUtils.requireRetry {

                }
            }

            override fun onResponse(call: Call, response: Response) {
                val result = Gson().fromJson(response.body?.string(), FollowGroup::class.java)
                MainScope().launch {
                    val temp = result.data.removeFirst()
                    binding.viewPager.adapter = adapter
                    adapter.submitList(result.data + temp)
                }
            }

        })
    }
}