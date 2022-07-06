package cn.spacexc.wearbili.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.dataclass.HorizontalButtonData
import cn.spacexc.wearbili.dataclass.user.User
import cn.spacexc.wearbili.manager.UserManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.Executors

/**
 * Created by XC-Qan on 2022/6/30.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class UserHorizontalButtonAdapter(context: Activity) :
    ListAdapter<HorizontalButtonData, UserHorizontalButtonAdapter.ButtonViewHolder>(object :
        DiffUtil.ItemCallback<HorizontalButtonData>() {
        override fun areItemsTheSame(
            oldItem: HorizontalButtonData,
            newItem: HorizontalButtonData
        ): Boolean {
            return false
        }

        override fun areContentsTheSame(
            oldItem: HorizontalButtonData,
            newItem: HorizontalButtonData
        ): Boolean {
            return false
        }

    }) {
    val context: Activity

    init {
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        return ButtonViewHolder(
            inflater.inflate(
                R.layout.cell_card_horizontal_button_large_avatar,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ButtonViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        UserManager.getUserById((getItem(position).mainText).toLong(), object : okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                holder.icon.setImageResource(R.drawable.default_avatar)
                holder.name.text = getItem(position).description
                when (position) {
                    0 -> {
                        holder.name.text = "XCちゃん"
                    }
                    1 -> {
                        holder.name.text = "Rechrd-Qan"
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val user = Gson().fromJson(response.body?.string(), User::class.java)
                holder.name.text = user.data.name
                holder.description.text = getItem(position).description
                try {
                    Executors.newCachedThreadPool().execute {
                        context.runOnUiThread {
                            Glide.with(context).load(user.data.face).skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .placeholder(R.drawable.default_avatar).circleCrop()
                                .into(holder.icon)
                        }
                    }
                } catch (e: OutOfMemoryError) {

                }
            }

        })
    }

    class ButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView
        val name: TextView
        val description: TextView

        init {
            icon = itemView.findViewById(R.id.icon)
            name = itemView.findViewById(R.id.mainText)
            description = itemView.findViewById(R.id.description)
        }
    }
}