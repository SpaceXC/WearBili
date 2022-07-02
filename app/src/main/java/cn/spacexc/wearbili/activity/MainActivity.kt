package cn.spacexc.wearbili.activity

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.databinding.ActivityMainBinding
import cn.spacexc.wearbili.utils.TimeUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
class MainActivity : AppCompatActivity(), Parcelable {
    @IgnoredOnParcel
    private lateinit var binding: ActivityMainBinding

    companion object {
        var currentPageId: MutableLiveData<Int> = MutableLiveData(R.id.recommendFragment)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        if (cn.spacexc.wearbili.manager.UserManager.getUserCookie() == null) navController.navigate(
            R.id.profileFragment
        )
        binding.pageName.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            //intent.putExtra("this", this)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        currentPageId.observe(this) {
            navController.navigate(it)
            binding.pageName.text = (when (it) {
                R.id.recommendFragment -> "推荐"
                R.id.searchFragment -> "搜索"
                R.id.profileFragment -> "我的"
                else -> ""
            })
        }

        lifecycleScope.launch {
            while (true) {
                binding.timeText.text = TimeUtils.getCurrentTime()
                delay(500)
            }
        }
    }


}
