package cn.spacexc.wearbili.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.dataclass.follow.FollowGroupUserItem
import cn.spacexc.wearbili.manager.UserManager
import cn.spacexc.wearbili.utils.ToastUtils
import com.bumptech.glide.Glide
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

/**
 * Created by XC-Qan on 2022/8/12.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class FollowedUserAdapter(private val context: Context) :
    ListAdapter<FollowGroupUserItem, FollowedUserAdapter.FollowedUserViewHolder>(object :
        DiffUtil.ItemCallback<FollowGroupUserItem>() {
        override fun areItemsTheSame(
            oldItem: FollowGroupUserItem,
            newItem: FollowGroupUserItem
        ): Boolean {
            return oldItem.mid == newItem.mid
        }

        override fun areContentsTheSame(
            oldItem: FollowGroupUserItem,
            newItem: FollowGroupUserItem
        ): Boolean {
            return oldItem.uname == newItem.uname
        }

    }) {

    private var isFollowed = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowedUserViewHolder {
        return FollowedUserViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.cell_follow_list, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FollowedUserViewHolder, position: Int) {
        val user = getItem(position)
        holder.username.text = user.uname
        Glide.with(context).load(user.face).circleCrop().placeholder(R.drawable.default_avatar)
            .into(holder.avatar)
        holder.isFollowed.setBackgroundResource(R.drawable.background_grey)
        holder.isFollowed.text = "已关注"
        holder.isFollowed.setOnClickListener {
            followUser(isFollowed, user.mid, holder)
        }
    }

    private fun followUser(isFollowed: Boolean, mid: Long, holder: FollowedUserViewHolder) {
        if (!isFollowed) {
            UserManager.subscribeUser(mid, 14, object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    MainScope().launch {
                        ToastUtils.makeText(
                            "关注失败了"
                        ).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    MainScope().launch {
                        ToastUtils.makeText(
                            "关注成功了"
                        ).show()
                        holder.isFollowed.setBackgroundResource(R.drawable.background_grey)
                        holder.isFollowed.text = "已关注"
                        this@FollowedUserAdapter.isFollowed = true
                    }


                }

            })
        } else {
            UserManager.deSubscribeUser(mid, 14, object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    MainScope().launch {
                        ToastUtils.makeText(
                            "取关失败了"
                        ).show()

                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    MainScope().launch {
                        ToastUtils.makeText(
                            "取关成功了"
                        ).show()
                        holder.isFollowed.setBackgroundResource(R.drawable.background_small_circle)
                        holder.isFollowed.text = "关注"
                        this@FollowedUserAdapter.isFollowed = false

                    }

                }

            })
        }
    }


    class FollowedUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatar: ImageView
        val username: TextView
        val isFollowed: TextView

        init {
            itemView.apply {
                avatar = findViewById(R.id.avatar)
                username = findViewById(R.id.username)
                isFollowed = findViewById(R.id.isFollowed)
            }
        }
    }
}