package cn.spacexc.wearbili.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target


/**
 * Created by XC-Qan on 2022/6/11.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

object GlideUtils {
    /**
     *
     * @param context context对象
     * @param imageUrl 图片网络地址
     * @param errorImageId 载入失败图片网络地址
     * @param placeImageId 占位图片网络地址
     * @param imageView 要载入图片的ImageView对象
     */
    fun loadPicsFitWidth(
        context: Context,
        imageUrl: String?,
        errorImageId: Int,
        placeImageId: Int,
        imageView: ImageView
    ) {
        Glide.with(context).load(imageUrl).skipMemoryCache(true)
            .listener(object : RequestListener<Drawable?> {


                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    /*
* 在此处填写具体载入逻辑*/

                    /*
                    判断imageView对象是否为空
                     */
                    /*
                    判断imageView的填充方式,如果不是fitxy的填充方式 设置其填充方式
                     */if (imageView.getScaleType() !== ImageView.ScaleType.FIT_XY) {
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY)
                    }
                    /*
                    进行宽度为matchparent时的适应imageView的高度计算
                     */
                    val params: ViewGroup.LayoutParams = imageView.getLayoutParams()
                    val vw: Int =
                        imageView.getWidth() - imageView.getPaddingLeft() - imageView.getPaddingRight()
                    val scale = vw.toFloat() / resource!!.intrinsicWidth.toFloat()
                    val vh = Math.round(resource.intrinsicHeight * scale)
                    params.height = vh + imageView.getPaddingTop() + imageView.getPaddingBottom()
                    imageView.setLayoutParams(params)
                    return false
                }

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            }).fitCenter().placeholder(placeImageId).error(errorImageId)
            .into(imageView)
    }

}