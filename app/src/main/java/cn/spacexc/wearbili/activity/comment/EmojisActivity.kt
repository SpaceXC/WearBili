package cn.spacexc.wearbili.activity.comment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.spacexc.wearbili.databinding.ActivityEmojisBinding

class EmojisActivity : AppCompatActivity() {
    lateinit var binding: ActivityEmojisBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmojisBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}