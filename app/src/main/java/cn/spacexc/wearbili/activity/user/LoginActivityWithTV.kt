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
import cn.spacexc.wearbili.utils.*
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

/**
 * Created by XC-Qan on 2022/7/28.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class LoginActivityWithTV : AppCompatActivity() {

    lateinit var qrImageView: ImageView
    private lateinit var back: TextView
//  private lateinit var time: TextView
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
        val baseUrl = "https://passport.bilibili.com/x/passport-tv-login/qrcode/auth_code"
        val params = "appkey=4409e2ce8ffd12b8&local_id=0&ts=${System.currentTimeMillis()}"
        val sign = EncryptUtils.getAppSign(EncryptUtils.AppSignType.TYPE_TV, params)
        val url = "$baseUrl?$params&sign=$sign"
        NetworkUtils.postUrl(url, FormBody.Builder().build(), object : Callback {
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
                        try {
                            Glide.with(this@LoginActivityWithTV)
                                .load(bitmap)
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .into(qrImageView)

                        } catch (e: OutOfMemoryError) {

                        }

                        lifecycleScope.launch {
                            while (true) {
                                val baseUrl2 =
                                    "http://passport.bilibili.com/x/passport-tv-login/qrcode/poll"
                                val params2 =
                                    "appkey=4409e2ce8ffd12b8&auth_code=${qrCode.data.oauthKey}&local_id=0&ts=${System.currentTimeMillis()}"
                                Log.d(Application.TAG, "onResponse: params: $params2")
                                val sign2 = EncryptUtils.getAppSign(
                                    EncryptUtils.AppSignType.TYPE_TV,
                                    params2
                                )
                                val body: FormBody = FormBody.Builder()
                                    //.add("oauthKey", qrCode.data.oauthKey)
                                    .build()
                                val url2 = "$baseUrl2?$params2&sign=$sign2"
                                NetworkUtils.postUrl(
                                    url2,
                                    body,
                                    object : Callback {
                                        override fun onFailure(call: Call, e: IOException) {
                                            this@LoginActivityWithTV.runOnUiThread {
                                                //next.isEnabled = true
                                                ToastUtils.makeText(
                                                    "网络连接错误"
                                                ).show()
                                            }
                                        }

                                        override fun onResponse(
                                            call: Call,
                                            response: Response
                                        ) {
                                            this@LoginActivityWithTV.runOnUiThread {
                                                val codeStat: QrCodeLoginStat = Gson().fromJson(
                                                    response.body?.string(),
                                                    QrCodeLoginStat::class.java
                                                )
                                                when (codeStat.code) {
                                                    0 -> {
                                                        ToastUtils.makeText(
                                                            "登录成功"
                                                        ).show()
                                                        cancel()
                                                        val intent = Intent(
                                                            this@LoginActivityWithTV,
                                                            SplashScreenActivity::class.java
                                                        )
                                                        intent.flags =
                                                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                                        startActivity(intent)
                                                    }
                                                    //-1.0 -> ToastUtils.makeText(this@LoginActivityWithTV, "密钥错误", Toast.LENGTH_SHORT).show()
                                                    86038 -> {
                                                        scanStat.text = "二维码过期了"
                                                        qrImageView.setImageResource(R.drawable.retry)
                                                        cancel()
                                                    }

                                                    86039 -> {
                                                        scanStat.text = "扫描二维码以登录"
                                                    }
                                                    else -> {
                                                        Log.d(
                                                            Application.TAG,
                                                            "onResponse: $codeStat"
                                                        )
                                                        scanStat.text = "未知错误，请重试"
                                                        qrImageView.setImageResource(R.drawable.retry)
                                                        cancel()
                                                    }
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