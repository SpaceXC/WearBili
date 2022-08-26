package cn.spacexc.wearbili.customview

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

class AutoFocusRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : RecyclerView(context, attrs) {
    init {
        isFocusable = true
    }

    override fun isFocused(): Boolean {
        return true
    }
}