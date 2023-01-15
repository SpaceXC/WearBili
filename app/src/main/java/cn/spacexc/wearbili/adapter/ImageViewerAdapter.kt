package cn.spacexc.wearbili.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.dataclass.dynamic.new.list.DrawItem
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView

/**
 * Created by XC-Qan on 2022/7/17.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class ImageViewerAdapter(val context: Context) :
    ListAdapter<DrawItem, ImageViewerAdapter.ImageViewerViewHolder>(object :
        DiffUtil.ItemCallback<DrawItem>() {
        override fun areItemsTheSame(oldItem: DrawItem, newItem: DrawItem): Boolean {
            return oldItem.src == newItem.src
        }

        override fun areContentsTheSame(oldItem: DrawItem, newItem: DrawItem): Boolean {
            return oldItem.src == newItem.src
        }

    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewerViewHolder {
        return ImageViewerViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.cell_image_viewer, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ImageViewerViewHolder, position: Int) {
        try {
            Glide.with(context).load(getItem(position).src)
                /*.listener(object : RequestListener<Drawable> {

                })*/
                .into(holder.photoView)
        } catch (_: OutOfMemoryError) {

        }
    }

    class ImageViewerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photoView: PhotoView

        init {
            photoView = itemView.findViewById(R.id.dynamicImageView)
        }
    }


}