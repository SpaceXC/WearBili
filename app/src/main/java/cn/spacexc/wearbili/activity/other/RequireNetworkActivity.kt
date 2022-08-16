package cn.spacexc.wearbili.activity.other

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.utils.TimeUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.Serializable

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
            (intent.getSerializableExtra("callback") as () -> Unit).invoke()
        }
    }

    class RetryCallback(callback: () -> Unit) : Serializable {
        var call = callback as Serializable
    }

    interface Retry {
        fun onRetry()
    }
}