package cn.spacexc.wearbili.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.activity.user.StarItemActivity
import cn.spacexc.wearbili.dataclass.star.StarFolderData
import cn.spacexc.wearbili.dataclass.star.StarListObj
import cn.spacexc.wearbili.manager.UserManager
import cn.spacexc.wearbili.utils.NetworkUtils
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

/**
 * Created by XC-Qan on 2022/8/11.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class StarFolderAdapter(private val context: Context) :
    ListAdapter<StarListObj, StarFolderAdapter.StarFolderViewHolder>(object :
        DiffUtil.ItemCallback<StarListObj>() {
        override fun areItemsTheSame(oldItem: StarListObj, newItem: StarListObj): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: StarListObj, newItem: StarListObj): Boolean {
            return oldItem.id == newItem.id
        }

    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StarFolderViewHolder {
        return StarFolderViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.cell_star_folder, parent, false)
        )
    }

    override fun onBindViewHolder(holder: StarFolderViewHolder, position: Int) {
        val folder = getItem(position)
        UserManager.getStarFolderData(folder.id, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                NetworkUtils.requireRetry {
                    onBindViewHolder(holder, holder.absoluteAdapterPosition)
                }

            }

            override fun onResponse(call: Call, response: Response) {
                val result = Gson().fromJson(response.body?.string(), StarFolderData::class.java)
                MainScope().launch {
                    Glide.with(context).load(result.data.cover).placeholder(R.drawable.placeholder)
                        .into(holder.cover)
                }
            }

        })
        holder.itemCount.text = "${folder.media_count}/1000"
        holder.title.text = folder.title
        holder.itemView.setOnClickListener {
            val intent = Intent(context, StarItemActivity::class.java)
            intent.putExtra("folderId", folder.id)
            context.startActivity(intent)
        }
    }

    class StarFolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cover: ImageFilterView
        val title: TextView
        val itemCount: TextView

        init {
            cover = itemView.findViewById(R.id.cover)
            title = itemView.findViewById(R.id.title)
            itemCount = itemView.findViewById(R.id.itemCount)
        }
    }
}