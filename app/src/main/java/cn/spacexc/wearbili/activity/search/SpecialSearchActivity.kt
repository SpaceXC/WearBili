package cn.spacexc.wearbili.activity.search

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.activity.other.QRCodeActivity

class SpecialSearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_special_search)
        findViewById<ConstraintLayout>(R.id.getSomeEnergy).setOnClickListener {
            Intent(this, QRCodeActivity::class.java).apply {
                //data = Uri.parse("android.resource://$packageName/${R.drawable.sp_search_one}")
                putExtra("extendMessage", "扫码打开能量加油站")
                putExtra("qrCodeUrl", "https://www.bilibili.com/blackboard/dynamic/306424")
                putExtra("pageName", "能量加油站")
                startActivity(this)
            }
        }
    }
}