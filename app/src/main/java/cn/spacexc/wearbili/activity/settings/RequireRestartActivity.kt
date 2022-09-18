package cn.spacexc.wearbili.activity.settings

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.utils.TimeUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RequireRestartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_require_restart)
        lifecycleScope.launch {
            while (true) {
                findViewById<TextView>(R.id.timeText).text = TimeUtils.getCurrentTime()
                delay(500)
            }
        }
        /**
         * From [CSDN](https://blog.csdn.net/lebulangzhen/article/details/108789777)
         */
        findViewById<ConstraintLayout>(R.id.restart).setOnClickListener {
            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                packageManager.getLaunchIntentForPackage(packageName)
            } else {
                TODO("VERSION.SDK_INT < CUPCAKE")
            }
            intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            //杀掉以前进程
            android.os.Process.killProcess(android.os.Process.myPid())

        }
    }
}