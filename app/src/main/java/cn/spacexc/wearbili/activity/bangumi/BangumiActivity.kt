package cn.spacexc.wearbili.activity.bangumi

import OnClickListerExtended
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.viewpager2.widget.ViewPager2
import cn.spacexc.wearbili.adapter.BangumiViewPagerAdapter
import cn.spacexc.wearbili.databinding.ActivityVideoBinding
import cn.spacexc.wearbili.fragment.CommentFragment
import cn.spacexc.wearbili.manager.ID_TYPE_EPID
import cn.spacexc.wearbili.viewmodel.BangumiViewModel


class BangumiActivity : AppCompatActivity() {
    val viewModel by viewModels<BangumiViewModel>()
    private lateinit var binding: ActivityVideoBinding

    lateinit var idType: String
    var cid: Long = 0L
    lateinit var id: String

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        id = intent.getStringExtra("id") ?: ""
        idType = intent.getStringExtra("idType") ?: ID_TYPE_EPID
        cid = intent.getLongExtra("cid", 0L)
        viewModel.getBangumi(
            idType = intent.getStringExtra("idType") ?: ID_TYPE_EPID,
            id = intent.getStringExtra("id") ?: ""
        )
        binding.viewPager2.adapter = BangumiViewPagerAdapter(this)
        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.pageName.text = (when (position) {
                    0 -> "剧集详情"
                    1 -> "单集评论"
                    else -> ""
                })
            }
        })
        binding.titleBar.setOnTouchListener(OnClickListerExtended(object :
            OnClickListerExtended.OnClickCallback {
            override fun onSingleClick() {
            }

            override fun onDoubleClick() {
                val fragment =
                    supportFragmentManager.findFragmentByTag("f${binding.viewPager2.currentItem}")
                when (binding.viewPager2.currentItem) {
                    1 -> {
                        (fragment as CommentFragment).apply {
                            refresh()
                        }
                    }
                }
            }

        }))
//        lifecycleScope.launch {
//            while (true) {
//                binding.timeText.text = TimeUtils.getCurrentTime()
//                delay(500)
//            }
//        }
        binding.pageName.setOnClickListener { finish() }
    }
}