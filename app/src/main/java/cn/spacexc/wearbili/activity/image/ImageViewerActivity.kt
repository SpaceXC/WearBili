package cn.spacexc.wearbili.activity.image

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.adapter.ImageViewerAdapter
import cn.spacexc.wearbili.dataclass.dynamic.dynamicimage.card.Picture

class ImageViewerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_viewer)
        val list = intent.getParcelableArrayListExtra<Picture>("imageList")
        val textview = findViewById<TextView>(R.id.textView10)
        val viewPager2 = findViewById<ViewPager2>(R.id.imageViewerPager)
        viewPager2.adapter = ImageViewerAdapter(this).apply { submitList(list) }
        viewPager2.setCurrentItem(intent.getIntExtra("currentPhotoItem", 0), false)
        textview.text = "${viewPager2.currentItem + 1}/${list?.size}"
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                textview.text = "${position + 1}/${list?.size}"
            }
        })
    }
}