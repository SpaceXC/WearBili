package cn.spacexc.wearbili.activity.image

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.spacexc.wearbili.R
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView

class PhotoViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_view)
        val photoView : PhotoView = findViewById(R.id.photoView)
        val url = intent.getStringExtra("imageUrl")
        Glide.with(this).load(url).into(photoView)
    }
}