package cn.spacexc.wearbili.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.dataclass.LoginQrCode
import cn.spacexc.wearbili.dataclass.QrCodeLoginStats
import cn.spacexc.wearbili.utils.NetworkUtils
import cn.spacexc.wearbili.utils.QRCodeUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class LoginActivity : AppCompatActivity() {
    val mThreadPool: ExecutorService = Executors.newCachedThreadPool()
    lateinit var qrImageView: ImageView
    private lateinit var back: TextView
    private lateinit var time: TextView
    lateinit var scanStat: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        qrImageView = findViewById(R.id.qrImage)
        back = findViewById(R.id.goBack)
        time = findViewById(R.id.time)
        scanStat = findViewById(R.id.scanStat)
        qrImageView.setOnClickListener { refreshQrCode() }
        back.setOnClickListener { finish() }
        lifecycleScope.launch{
            @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat("HH:mm")
            while (true){
                val date = sdf.format(Date())
                time.text = date
                delay(100)
            }
        }
        refreshQrCode()
    }

    private fun refreshQrCode(){
        qrImageView.isEnabled = false
        qrImageView.setImageResource(R.drawable.loading)
        NetworkUtils.getUrl("https://passport.bilibili.com/qrcode/getLoginUrl", object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                mThreadPool.execute {
                    this@LoginActivity.runOnUiThread {
                        Toast.makeText(this@LoginActivity, "获取登录二维码失败", Toast.LENGTH_SHORT).show()
                        qrImageView.isEnabled = true
                        qrImageView.setImageResource(R.drawable.retry)
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                mThreadPool.execute{
                    this@LoginActivity.runOnUiThread{
                        qrImageView.isEnabled = true
                        if(response.code == 200) {
                            val qrCode: LoginQrCode =
                                Gson().fromJson(response.body?.string(), LoginQrCode::class.java)
                            Log.d(Application.getTag(), "onResponse: ${qrCode.data.url}")
                            val bitmap: Bitmap? =
                                QRCodeUtil.createQRCodeBitmap(qrCode.data.url, 64, 64)
                            //qrImageView.setImageBitmap(bitmap)
                            try {
                                Glide.with(this@LoginActivity)
                                    .load(bitmap)
                                    .skipMemoryCache(true)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .into(qrImageView)

                            } catch (e: OutOfMemoryError) {

                            }

                            lifecycleScope.launch {
                                while (true) {
                                    val body: FormBody = FormBody.Builder()
                                        .add("oauthKey", qrCode.data.oauthKey)
                                        .build()
                                    NetworkUtils.postUrl(
                                        "https://passport.bilibili.com/qrcode/getLoginInfo",
                                        body,
                                        object : Callback {
                                            override fun onFailure(call: Call, e: IOException) {
                                                this@LoginActivity.runOnUiThread {
                                                    //next.isEnabled = true
                                                Toast.makeText(this@LoginActivity, "网络连接错误", Toast.LENGTH_SHORT).show()
                                            }
                                        }

                                        override fun onResponse(call: Call, response: Response) {
                                            this@LoginActivity.runOnUiThread {
                                                val codeStat : QrCodeLoginStats = Gson().fromJson(response.body?.string(), QrCodeLoginStats::class.java)
                                                if(codeStat.status){
                                                    Toast.makeText(
                                                        this@LoginActivity,
                                                        "登录成功",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    cancel()
                                                    if (intent.getBooleanExtra("fromHome", true)) {
                                                        val intent = Intent(
                                                            this@LoginActivity,
                                                            MainActivity::class.java
                                                        )
                                                        intent.flags =
                                                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                                        startActivity(intent)
                                                    } else {
                                                        finish()
                                                    }
                                                }
                                                else{
                                                    when(codeStat.data as Number){
                                                        //-1.0 -> Toast.makeText(this@LoginActivity, "密钥错误", Toast.LENGTH_SHORT).show()
                                                        -2.0 -> {
                                                            scanStat.text = "二维码过期了"
                                                            qrImageView.setImageResource(R.drawable.retry)
                                                            cancel()
                                                        }
                                                        -4.0 -> {
                                                            scanStat.text = "使用手机客户端扫描二维码登入"
                                                        }
                                                        -5.0 -> {
                                                            scanStat.text = "请在手机上轻触确认"
                                                        }
                                                        else -> {
                                                            scanStat.text = "未知错误，请重试"
                                                            qrImageView.setImageResource(R.drawable.retry)
                                                            cancel()
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                    })
                                    Log.d(Application.getTag(), "onResponse: 轮询中...")
                                    delay(1500)
                                }
                            }
                        }
                        else{
                            Toast.makeText(this@LoginActivity, "获取登录二维码失败", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        })
    }
}