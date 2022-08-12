package cn.spacexc.wearbili.activity.video

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import cn.spacexc.wearbili.adapter.VideoViewPagerAdapter
import cn.spacexc.wearbili.databinding.ActivityVideoBinding
import cn.spacexc.wearbili.dataclass.VideoInfoData
import cn.spacexc.wearbili.utils.TimeUtils
import cn.spacexc.wearbili.utils.VideoUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class VideoActivity : AppCompatActivity() {
    private lateinit var binding : ActivityVideoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.viewPager2.adapter = VideoViewPagerAdapter(this)
        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.pageName.text = (when (position) {
                    0 -> "详情"
                    1 -> "评论"
                    else -> ""
                })
            }
        })
        lifecycleScope.launch {
            while (true) {
                binding.timeText.text = TimeUtils.getCurrentTime()
                delay(500)
            }
        }
        binding.pageName.setOnClickListener { finish() }
        //TimeThread(binding.timeText, binding.viewPager2, "VideoPage").start()

    }

    var currentVideo: VideoInfoData? = null
    var isInitialized = false

    fun getId() : String? {
        if (!intent.getStringExtra("videoId")
                .isNullOrBlank()
        ) return intent.getStringExtra("videoId")
        if (!intent.data?.path.isNullOrBlank()) return VideoUtils.av2bv(
            "av${
                intent.data?.path?.replace(
                    "/",
                    ""
                )!!
            }"
        )
        if (!intent.data?.getQueryParameter("bvid")
                .isNullOrEmpty()
        ) return intent.data?.getQueryParameter("bvid")
        return null
    }

    fun setPage(page: Int) {
        binding.viewPager2.currentItem = page - 1
    }

    // Activity中
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // 获取到Activity下的Fragment
        val fragments = supportFragmentManager.fragments
        // 查找在Fragment中onRequestPermissionsResult方法并调用
        for (fragment in fragments) {
            fragment?.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    /*private var startY = 0f //手指按下时的Y坐标

    private var startX = 0f //手指按下时的Y坐标*/

    /*override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(binding.viewPager2.currentItem == 1){
            Log.i(Application.getTag(), "onTouch事件被触发了")
            val screenWidth: Int = ScreenUtils.getScreenWidth()
            Log.i(Application.getTag(), "screenWidth:$screenWidth")
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.x
                    startY = event.y
                }
                MotionEvent.ACTION_MOVE -> {
                    val endY = event.y
                    val distanceY = startY - endY
                    if (startX > screenWidth / 2) {
                        //右边
                        //在这里处理音量
                        Log.i(Application.getTag(), "音量+-")
                        val FLING_MIN_DISTANCE = 0.5
                        val FLING_MIN_VELOCITY = 0.5
                        val am = ContextCompat.getSystemService(
                            Application.getContext(),
                            AudioManager::class.java
                        )
                        if (distanceY > FLING_MIN_DISTANCE && abs(distanceY) > FLING_MIN_VELOCITY) {
                            Log.i(Application.getTag(), "音量++")
                            am?.adjustStreamVolume(
                                AudioManager.STREAM_MUSIC,
                                AudioManager.ADJUST_RAISE,
                                AudioManager.FLAG_SHOW_UI
                            )
                        }
                        if (distanceY < FLING_MIN_DISTANCE && abs(distanceY) > FLING_MIN_VELOCITY) {
                            Log.i(Application.getTag(), "音量--")
                            am?.adjustStreamVolume(
                                AudioManager.STREAM_MUSIC,
                                AudioManager.ADJUST_LOWER,
                                AudioManager.FLAG_SHOW_UI
                            )
                        }
                    } else {
                        Log.i(Application.getTag(), "屏幕亮度+-")
                        //屏幕左半部分上滑，亮度变大，下滑，亮度变小
                        val FLING_MIN_DISTANCE = 0.5
                        val FLING_MIN_VELOCITY = 0.5
                        if (distanceY > FLING_MIN_DISTANCE && abs(distanceY) > FLING_MIN_VELOCITY) {
                            var brightness: Int = BrightnessUtils.getWindowBrightness(window)
                            Log.i(Application.getTag(), "屏幕亮度++,brightness:$brightness")
                            if (brightness < 255) {
                                BrightnessUtils.setWindowBrightness(window, ++brightness)
                            }
                        }
                        if (distanceY < FLING_MIN_DISTANCE && abs(distanceY) > FLING_MIN_VELOCITY) {
                            //BrightnessUtils.setBrightness(BrightnessUtils.getBrightness()+10);
                            var brightness: Int = BrightnessUtils.getWindowBrightness(window)
                            Log.i(Application.getTag(), "屏幕亮度--,brightness:$brightness")
                            if (brightness > 0) {
                                BrightnessUtils.setWindowBrightness(window, --brightness)
                            }
                        }
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }*/
}