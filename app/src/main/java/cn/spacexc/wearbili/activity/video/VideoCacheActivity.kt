package cn.spacexc.wearbili.activity.video

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import cn.spacexc.wearbili.databinding.ActivityVideoCacheBinding
import cn.spacexc.wearbili.utils.TimeUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VideoCacheActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoCacheBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoCacheBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.pageName.setOnClickListener { finish() }
        lifecycleScope.launch {
            while (true) {
                binding.timeText.text = TimeUtils.getCurrentTime()
                delay(1000)
                Long
            }
        }
    }
}