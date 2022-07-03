package cn.spacexc.wearbili.activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.utils.QRCodeUtil
import com.bumptech.glide.Glide
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class PlayOnPhoneActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_on_phone)
        val bitmap: Bitmap? =
            QRCodeUtil.createQRCodeBitmap(intent.getStringExtra("qrCodeUrl"), 128, 128)
        Glide.with(this@PlayOnPhoneActivity)
            .load(bitmap)
            .into(findViewById(R.id.qrImage2))

        findViewById<TextView>(R.id.pageName).setOnClickListener { finish() }
        lifecycleScope.launch {
            @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat("HH:mm")
            while (true) {
                val date = sdf.format(Date())
                findViewById<TextView>(R.id.timeText).text = date
                delay(100)
            }
        }
    }
}