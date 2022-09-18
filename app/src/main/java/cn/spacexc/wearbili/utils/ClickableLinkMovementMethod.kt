package cn.spacexc.wearbili.utils

import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


/**
 * Created by XC-Qan on 2022/8/13.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class CustomLinkMovementMethod(private val view: View?) : LinkMovementMethod() {
    override fun onTouchEvent(widget: TextView, buffer: Spannable?, event: MotionEvent): Boolean {
        val b = super.onTouchEvent(widget, buffer, event)
        //解决点击事件冲突问题
        if (!b && event.action == MotionEvent.ACTION_UP) {
            val parent = widget.parent //处理widget的父控件点击事件
            if (parent is ViewGroup) {
                return parent.performClick()
            }
            view?.performClick()
        }
        return b
    }

    companion object {
        fun getInstance(view: View?): CustomLinkMovementMethod? {
            if (sInstance == null) sInstance = CustomLinkMovementMethod(view)
            return sInstance
        }

        private var sInstance: CustomLinkMovementMethod? = null
    }
}