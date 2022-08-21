package cn.spacexc.wearbili.activity.image

import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.adapter.ImageViewerAdapter
import cn.spacexc.wearbili.dataclass.dynamic.dynamicimage.card.Picture
import cn.spacexc.wearbili.utils.LogUtils.log


class ImageViewerActivity : AppCompatActivity() {
    var isInfoVisible = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_viewer)
        val list = intent.getParcelableArrayListExtra<Picture>("imageList")
        val textview = findViewById<TextView>(R.id.textView10)
        val viewPager2 = findViewById<ViewPager2>(R.id.imageViewerPager)
        viewPager2.adapter = ImageViewerAdapter(this).apply { submitList(list) }
        viewPager2.setCurrentItem(intent.getIntExtra("currentPhotoItem", 0), false)
        textview.text = "${viewPager2.currentItem + 1}/${list?.size}"
        findViewById<FrameLayout>(R.id.frame).setOnClickListener {
            "Clicked".log()
            toggleInfoVisibility()

        }
        findViewById<ImageView>(R.id.pageName).setOnClickListener { finish() }
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                textview.text = "${position + 1}/${list?.size}"
            }
        })
        viewPager2.setOnLongClickListener {
            finish()
            false
        }

        //toggleInfoVisibility()
    }

    private fun toggleInfoVisibility() {
        val info = findViewById<ConstraintLayout>(R.id.picInfo)
        /*info.isVisible = !isInfoVisible
        isInfoVisible = !isInfoVisible*/
        val appearAnimation = AlphaAnimation(0f, 1f)
        appearAnimation.duration = 250
        val disappearAnimation = AlphaAnimation(1f, 0f)
        disappearAnimation.duration = 250

        isInfoVisible = if (isInfoVisible) {
            info.startAnimation(disappearAnimation)
            disappearAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {
                }

                override fun onAnimationEnd(p0: Animation?) {
                    info.isVisible = false
                }

                override fun onAnimationRepeat(p0: Animation?) {
                }

            })
            !isInfoVisible
        } else {
            info.startAnimation(appearAnimation)
            info.isVisible = true
            !isInfoVisible
        }
    }
}