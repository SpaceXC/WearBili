package cn.spacexc.wearbili.activity.other

import android.os.Bundle
import android.os.Parcelable
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.utils.TimeUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

class RequireNetworkActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_require_network)
        lifecycleScope.launch {
            while (true) {
                findViewById<TextView>(R.id.timeText).text = TimeUtils.getCurrentTime()
                delay(500)
            }
        }
        findViewById<ConstraintLayout>(R.id.retry).setOnClickListener {
            (intent.getParcelableExtra<RetryCallback>("callback") as RetryCallback).callback.onRetry()
        }
    }

    @Parcelize
    class RetryCallback : Parcelable {
        lateinit var callback: Retry

        interface Retry {
            fun onRetry()
        }
    }
}