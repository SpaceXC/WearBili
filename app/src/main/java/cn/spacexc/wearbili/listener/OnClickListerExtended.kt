import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import kotlinx.coroutines.*

/**
 * Created by HeTingWei on 2018/4/30.
 * MODIFIED BY XC on 2022/7/7
 * From CSDN https://blog.csdn.net/htwhtw123/article/details/80150984
 */
class OnClickListerExtended(private val myClickCallBack: OnClickCallback) :
    OnTouchListener {
    private var clickCount = 0 //记录连续点击次数

    interface OnClickCallback {
        fun onSingleClick() //点击一次的回调
        fun onDoubleClick() //连续点击两次的回调
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        /*if (event.action == MotionEvent.ACTION_DOWN) {
            clickCount++
            handler.postDelayed({
                if (clickCount == 1) {
                    myClickCallBack.onSingleClick()
                } else if (clickCount == 2) {
                    myClickCallBack.onDoubleClick()
                }
                handler.removeCallbacksAndMessages(null)
                //清空handler延时，并防内存泄漏
                clickCount = 0 //计数清零
            }, timeout.toLong()) //延时timeout后执行run方法中的代码
        }
        return false //让点击事件继续传播，方便再给View添加其他事件监听*/
        v.performClick()
        if (event.action == MotionEvent.ACTION_DOWN) {
            clickCount++
            GlobalScope.launch {
                if (clickCount == 1) myClickCallBack.onSingleClick();
                else if (clickCount == 2) myClickCallBack.onDoubleClick()
                delay(timeout.toLong())
                clickCount = 0
                cancel()
                v.performClick()
            }
        }
        v.performClick()
        return false
    }

    companion object {
        private const val timeout = 400 //双击间四百毫秒延时
    }

}