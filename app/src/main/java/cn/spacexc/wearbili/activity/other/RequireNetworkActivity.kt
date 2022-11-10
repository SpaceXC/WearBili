package cn.spacexc.wearbili.activity.other

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.utils.RESULT_RETRY
import cn.spacexc.wearbili.utils.TimeUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RequireNetworkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_require_network)
        val requestDataLauncher =
            this.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_RETRY) {
                    val data = result.data?.getStringExtra("data")
                    // Handle data from SecondActivity
                }
            }
//        lifecycleScope.launch {
//            while (true) {
//                findViewById<TextView>(R.id.timeText).text = TimeUtils.getCurrentTime()
//                delay(500)
//            }
//        }
        findViewById<ConstraintLayout>(R.id.retry).setOnClickListener {
            val intent = Intent()
            setResult(RESULT_RETRY)
            finish()
        }
    }


    companion object {
        fun requireRetry(callback: () -> Unit) {
            val intent = Intent(Application.context, this::class.java)

        }
    }
}