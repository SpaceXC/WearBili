package cn.spacexc.wearbili.customview

import android.content.Context
import android.util.AttributeSet
import androidx.wear.widget.WearableRecyclerView

class AutoFocusWearableRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : WearableRecyclerView(context, attrs) {
    init {
        isFocusable = true
    }

    override fun isFocused(): Boolean {
        return true
    }
}