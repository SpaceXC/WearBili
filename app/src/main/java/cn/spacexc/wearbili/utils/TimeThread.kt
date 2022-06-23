package cn.spacexc.wearbili.utils

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by XC-Qan on 2022/6/8.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class TimeThread(tvDate: TextView, viewPager2: ViewPager2?, pageName: String?) : Thread() {
    @SuppressLint("HandlerLeak")
    private val mHandler: Handler = object : Handler() {
        @SuppressLint("SetTextI18n")
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 22) {
                @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat("HH:mm")
                val date = sdf.format(Date())
                var currentPage = "加载中..."
                if(pageName == "HomePage"){
                    currentPage = when(viewPager2?.currentItem){
                        0 -> "推荐"
                        1 -> "搜索"
                        else -> "我的"
                    }
                }
                if(pageName == "VideoPage"){
                    currentPage = when(viewPager2?.currentItem){
                        0 -> "信息"
                        1 -> "播放"
                        2 -> "评论"
                        else -> ""
                    }
                }
                if(!pageName.isNullOrEmpty() || viewPager2 != null) tvDate.text = "$currentPage | $date"
                else tvDate.text = date
            }
        }
    }

    /*fun getGreeting(): String {
        val currentHour: Int = Calendar.getInstance()[Calendar.HOUR_OF_DAY]
        Log.d(Application.getTag(), "getGreeting: $currentHour")
        return when(currentHour){
            in 5..11 -> "早上好"
            in 12..17 -> "下午好"
            in 18..23 -> "晚上好"
            else -> "晚安"
        }
    }*/
    override fun run() {
        do {
            try {
                sleep(1000)
                val msg = Message()
                msg.what = 22
                mHandler.sendMessage(msg)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        } while (true)
    }
}