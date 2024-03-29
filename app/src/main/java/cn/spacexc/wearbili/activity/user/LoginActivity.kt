package cn.spacexc.wearbili.activity.user

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.activity.other.SplashScreenActivity
import cn.spacexc.wearbili.dataclass.LoginQrCode
import cn.spacexc.wearbili.dataclass.QrCodeLoginStat
import cn.spacexc.wearbili.manager.UserManager
import cn.spacexc.wearbili.utils.NetworkUtils
import cn.spacexc.wearbili.utils.QRCodeUtil
import cn.spacexc.wearbili.utils.SharedPreferencesUtils
import cn.spacexc.wearbili.utils.ToastUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.Response
import java.io.IOException


class LoginActivity : AppCompatActivity() {

    lateinit var qrImageView: ImageView
    private lateinit var back: TextView
    private lateinit var time: TextView
    lateinit var scanStat: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        qrImageView = findViewById(R.id.qrImage)
        back = findViewById(R.id.pageName)
//      time = findViewById(R.id.timeText)
        scanStat = findViewById(R.id.scanStat)
        qrImageView.setOnClickListener { refreshQrCode() }
        back.setOnClickListener { finish() }
//        lifecycleScope.launch {
//            while (true) {
//                time.text = TimeUtils.getCurrentTime()
//                delay(500)
//            }
//        }
        refreshQrCode()
    }

    private fun refreshQrCode() {
        qrImageView.isEnabled = false
        qrImageView.setImageResource(R.drawable.loading)
        NetworkUtils.getUrl("https://passport.bilibili.com/qrcode/getLoginUrl", object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                MainScope().launch {
                    ToastUtils.makeText("获取登录二维码失败")
                        .show()
                    qrImageView.isEnabled = true
                    qrImageView.setImageResource(R.drawable.retry)

                }
            }

            override fun onResponse(call: Call, response: Response) {
                MainScope().launch {
                    qrImageView.isEnabled = true
                    if (response.code == 200) {
                        val qrCode: LoginQrCode =
                            Gson().fromJson(response.body?.string(), LoginQrCode::class.java)
                        Log.d(Application.getTag(), "onResponse: ${qrCode.data.url}")
                        val bitmap: Bitmap? =
                            QRCodeUtil.createQRCodeBitmap(
                                qrCode.data.url,
                                128,
                                128,
                                QRCodeUtil.ERROR_CORRECTION_M
                            )
                        //qrImageView.setImageBitmap(bitmap)
                        if (!this@LoginActivity.isDestroyed) {
                            try {
                                Glide.with(this@LoginActivity)
                                    .load(bitmap)
                                    .skipMemoryCache(true)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .into(qrImageView)

                            } catch (e: OutOfMemoryError) {

                            }
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
                                                ToastUtils.makeText(
                                                    "网络连接错误"
                                                ).show()
                                            }
                                        }

                                        override fun onResponse(
                                            call: Call,
                                            response: Response
                                        ) {
                                            val responseStr = response.body?.string()
                                            this@LoginActivity.runOnUiThread {
                                                try {
                                                    val codeStat: QrCodeLoginStat =
                                                        Gson().fromJson(
                                                            responseStr,
                                                            QrCodeLoginStat::class.java
                                                        )
                                                    if (codeStat.status) {
                                                        ToastUtils.showText("请稍后...")
                                                        UserManager.getAccessKey(object :
                                                            NetworkUtils.ResultCallback<String> {
                                                            override fun onSuccess(
                                                                result: String,
                                                                code: Int
                                                            ) {
                                                                MainScope().launch {
                                                                    SharedPreferencesUtils.saveString(
                                                                        "accessKey",
                                                                        result
                                                                    )
                                                                    ToastUtils.makeText("登录成功")
                                                                        .show()
                                                                    if (intent.getBooleanExtra(
                                                                            "fromHome",
                                                                            true
                                                                        )
                                                                    ) {
                                                                        val intent = Intent(
                                                                            this@LoginActivity,
                                                                            SplashScreenActivity::class.java
                                                                        )
                                                                        intent.flags =
                                                                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                                                        startActivity(intent)
                                                                    } else {
                                                                        finish()
                                                                    }
                                                                }
                                                            }

                                                            override fun onFailed(e: Exception?) {
                                                                MainScope().launch {
                                                                    ToastUtils.showText("登录失败！$e")
                                                                }
                                                            }

                                                        })
                                                    } else {
                                                        when (codeStat.data as Number) {
                                                            //-1.0 -> ToastUtils.makeText(this@LoginActivity, "密钥错误", Toast.LENGTH_SHORT).show()
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
                                                } catch (e: Exception) {

                                                }

                                            }
                                        }

                                    })
                                Log.d(Application.getTag(), "onResponse: 轮询中...")
                                delay(1500)
                            }
                        }
                    } else {
                        ToastUtils.makeText("获取登录二维码失败")
                            .show()
                    }
                }
            }


        })
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.cancel()
    }
}