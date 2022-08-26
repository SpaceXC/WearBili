package cn.spacexc.wearbili.customview

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.TextView

@SuppressLint("AppCompatCustomView")
class MarqueeTextView : TextView {
    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        initView()
    }

    private fun initView() {
        ellipsize = TextUtils.TruncateAt.MARQUEE
        isSingleLine = true
        isFocusable = true
        marqueeRepeatLimit = -1
    }

    override fun isFocused(): Boolean {
        return true
    }
}