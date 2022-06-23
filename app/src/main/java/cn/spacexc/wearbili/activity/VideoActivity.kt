package cn.spacexc.wearbili.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.spacexc.wearbili.adapter.VideoViewPagerAdapter
import cn.spacexc.wearbili.databinding.ActivityVideoBinding
import cn.spacexc.wearbili.dataclass.VideoInfoData
import cn.spacexc.wearbili.utils.TimeThread

class VideoActivity : AppCompatActivity() {
    private lateinit var binding : ActivityVideoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.viewPager2.adapter = VideoViewPagerAdapter(this)
        TimeThread(binding.timeText, binding.viewPager2, "VideoPage").start()
    }

    lateinit var currentVideo : VideoInfoData
    var isInitialized = false

    fun getId() : String? {
        return intent.getStringExtra("videoId")
    }

    fun setPage(page : Int){
        binding.viewPager2.currentItem = page - 1
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