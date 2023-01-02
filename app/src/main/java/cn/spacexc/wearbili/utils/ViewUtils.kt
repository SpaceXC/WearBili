package cn.spacexc.wearbili.utils

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View

/**
 * Created by Xiaochang on 2022/8/27.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */
object ViewUtils {
    /**
     * 添加点击缩放效果
     *
     * From [https://blog.csdn.net/lgz0921/article/details/119353183](https://blog.csdn.net/lgz0921/article/details/119353183)
     *
     * @author lgz0921, XC
     *
     * @param scale 缩小的倍数
     * @param alpha 缩小后的alpha值
     * @param duration 缩小动画的时间/ms
     */
    @SuppressLint("ClickableViewAccessibility")
    fun View.addClickScale(scale: Float = 0.9f, alpha: Float = 0.8f, duration: Long = 150) {
        this.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    this.animate().scaleX(scale).scaleY(scale).alpha(alpha).setDuration(duration).start()
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    this.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(duration).start()
                }
            }
            this.onTouchEvent(event)
        }
    }
}