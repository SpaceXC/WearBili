package cn.spacexc.wearbili.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.activity.user.SpaceProfileActivity
import cn.spacexc.wearbili.dataclass.HorizontalButtonData
import cn.spacexc.wearbili.dataclass.user.User
import cn.spacexc.wearbili.manager.UserManager
import cn.spacexc.wearbili.utils.ViewUtils.addClickScale
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Response
import java.io.IOException

/**
 * Created by XC-Qan on 2022/6/30.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class UserHorizontalButtonAdapter(
    val context: Context,
    private val customActions: List<String> = emptyList(),
    val customClickAction: (String) -> Unit = { }
) :
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
        if (getItem(position).mainText.contains("uid")) {
            val mid = (getItem(position).mainText.replace("uid", "")).toLong()
            holder.itemView.setOnClickListener {
                if (customActions.contains(getItem(position).mainText)) {
                    customClickAction(getItem(position).mainText)
                } else {
                    Intent(context, SpaceProfileActivity::class.java).apply {
                        putExtra("userMid", mid)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(this)
                    }
                }
            }
            holder.itemView.addClickScale()
            UserManager.getUserById(
                mid,
                object : okhttp3.Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        holder.icon.setImageResource(R.drawable.default_avatar)
                        holder.name.text = getItem(position).mainText
                        holder.description.text = getItem(position).description
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val user = Gson().fromJson(response.body?.string(), User::class.java)

                        try {
                            MainScope().launch {
                                holder.name.text = user.data.name
                                holder.description.text = getItem(position).description
                                Glide.with(context).load(user.data.face).skipMemoryCache(true)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .placeholder(R.drawable.default_avatar).circleCrop()
                                    .into(holder.icon)
                            }

                        } catch (_: OutOfMemoryError) {

                        }
                    }

                })
        } else {
            try {
                holder.name.text = getItem(position).mainText
                holder.description.text = getItem(position).description
                Glide.with(context).load(R.drawable.akari).skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.default_avatar).circleCrop()
                    .into(holder.icon)
                holder.itemView.addClickScale()
                holder.itemView.setOnClickListener {
                    customClickAction(getItem(position).mainText)
                }

            } catch (_: OutOfMemoryError) {

            }
        }

    }

    class ButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView
        val name: TextView
        val description: TextView

        init {
            icon = itemView.findViewById(R.id.uploaderAvatar)
            name = itemView.findViewById(R.id.usernameText)
            description = itemView.findViewById(R.id.survey)
        }
    }
}