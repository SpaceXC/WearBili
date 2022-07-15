package cn.spacexc.wearbili.utils

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.R

/**
 * Created by XC-Qan on 2022/7/14.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

object ToastUtils {
    private const val ContentID: Int = R.id.toastTextView
    private const val LayoutID: Int = R.layout.layout_toast_test
    fun makeText(content: String): Toast {
        val toast = Toast(Application.getContext())
        val view: View =
            LayoutInflater.from(Application.getContext()).inflate(LayoutID, null, false)
        toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL, 0, 0)
        toast.duration = Toast.LENGTH_SHORT
        val text: TextView = view.findViewById(ContentID)
        toast.view = view
        text.text = content
        return toast
    }
}