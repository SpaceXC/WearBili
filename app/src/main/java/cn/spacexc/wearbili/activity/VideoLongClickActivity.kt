package cn.spacexc.wearbili.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.spacexc.wearbili.databinding.ActivityVideoLongClickBinding

class VideoLongClickActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoLongClickBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoLongClickBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.goBack.setOnClickListener { finish() }
    }
}