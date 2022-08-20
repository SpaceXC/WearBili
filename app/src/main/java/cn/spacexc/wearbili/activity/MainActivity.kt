package cn.spacexc.wearbili.activity

import OnClickListerExtended
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.activity.other.MenuActivity
import cn.spacexc.wearbili.adapter.MainViewPagerAdapter
import cn.spacexc.wearbili.databinding.ActivityMainBinding
import cn.spacexc.wearbili.fragment.DynamicFragment
import cn.spacexc.wearbili.fragment.RecommendFragment
import cn.spacexc.wearbili.manager.UserManager
import cn.spacexc.wearbili.utils.TimeUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    companion object {
        var currentPageId: MutableLiveData<Int> = MutableLiveData(R.id.recommendFragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        /*val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController*/
        binding.viewpager.adapter = MainViewPagerAdapter(this)
        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.pageName.text = when (position) {
                    0 -> "推荐"
                    1 -> "动态"
                    2 -> "我的"
                    else -> ""
                }
            }
        })
        if (!UserManager.isLoggedIn()) binding.viewpager.currentItem = 2
        binding.pageName.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            //intent.putExtra("this", this)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            overridePendingTransition(R.anim.activity_in_y, R.anim.activity_out_y)
        }

        currentPageId.observe(this) {
            when (it) {
                R.id.recommendFragment -> {
                    binding.viewpager.currentItem = 0
                }
                R.id.profileFragment -> {
                    binding.viewpager.currentItem = 2
                }
                R.id.dynamicFragment -> {
                    binding.viewpager.currentItem = 1
                }
            }

            binding.pageName.text = (when (it) {
                R.id.recommendFragment -> "推荐"
                R.id.searchFragment -> "搜索"
                R.id.profileFragment -> "我的"

                else -> ""
            })
        }

        binding.titleBar.setOnTouchListener(OnClickListerExtended(object :
            OnClickListerExtended.OnClickCallback {
            override fun onSingleClick() {

            }

            override fun onDoubleClick() {
                val fragment =
                    supportFragmentManager.findFragmentByTag("f${binding.viewpager.currentItem}")
                when (binding.viewpager.currentItem) {
                    0 -> {
                        (fragment as RecommendFragment).apply {
                            refresh()
                        }
                    }
                    1 -> {
                        (fragment as DynamicFragment).apply {
                            refresh()
                        }
                    }
                }
            }

        }))

        lifecycleScope.launch {
            while (true) {
                binding.timeText.text = TimeUtils.getCurrentTime()
                delay(500)
            }
        }


    }


}
