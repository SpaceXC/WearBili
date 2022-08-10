package cn.spacexc.wearbili.activity.other

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.utils.QRCodeUtil
import cn.spacexc.wearbili.utils.TimeUtils
import com.bumptech.glide.Glide
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class QRCodeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode)
        findViewById<TextView>(R.id.extendMessage).text = intent.getStringExtra("extendMessage")
        findViewById<TextView>(R.id.pageName).text = intent.getStringExtra("pageName")
        //TODO 二维码activity图片显示&把手机播放迁移过来
        findViewById<ImageView>(R.id.backGround).setImageURI(
            intent.data
                ?: Uri.parse("android.resource://$packageName/${R.drawable.twotwo_threethree}")
        )
        val bitmap: Bitmap? =
            QRCodeUtil.createQRCodeBitmap(intent.getStringExtra("qrCodeUrl"), 128, 128)
        Glide.with(this)
            .load(bitmap)
            .into(findViewById(R.id.qrImage2))

        findViewById<TextView>(R.id.pageName).setOnClickListener { finish() }
        lifecycleScope.launch {
            while (true) {
                findViewById<TextView>(R.id.timeText).text = TimeUtils.getCurrentTime()
                delay(100)
            }
        }
    }
}