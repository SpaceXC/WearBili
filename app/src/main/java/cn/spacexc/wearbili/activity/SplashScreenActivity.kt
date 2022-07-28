package cn.spacexc.wearbili.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.manager.UserManager


class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        if (UserManager.isLoggedIn()) {
            val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
            //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out)
            finish()
        } else {
            val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
            //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out)
            finish()
        }
        /*lifecycleScope.launch {
            delay(1_500)
            if(!UserManager.isLoggedIn()){
                val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
                //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out)
                finish()
            }
            else{
                val query = LCQuery<LCObject>("ActivatedUIDs")
                query.whereEqualTo("uid", UserManager.getUid())
                query.findInBackground().subscribe(object : Observer<List<LCObject?>?> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(t: List<LCObject?>) {
                        val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                        //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out)
                        finish()
                    }

                    override fun onError(e: Throwable) {
                        e.localizedMessage?.let { ToastUtils.makeText(it).show() }
                    }

                    override fun onComplete() {

                    }

                })
            }
        }*/
    }
}