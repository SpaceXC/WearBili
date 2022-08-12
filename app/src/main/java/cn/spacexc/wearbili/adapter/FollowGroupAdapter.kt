package cn.spacexc.wearbili.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.dataclass.follow.FollowGroupUsers
import cn.spacexc.wearbili.dataclass.follow.Group
import cn.spacexc.wearbili.manager.UserManager
import cn.spacexc.wearbili.utils.NetworkUtils
import com.google.gson.Gson
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

class FollowGroupAdapter(private val context: Context) :
    ListAdapter<Group, FollowGroupAdapter.FollowGroupViewHolder>(object :
        DiffUtil.ItemCallback<Group>() {
        override fun areItemsTheSame(oldItem: Group, newItem: Group): Boolean {
            return oldItem.tagid == newItem.tagid
        }

        override fun areContentsTheSame(oldItem: Group, newItem: Group): Boolean {
            return oldItem.name == newItem.name
        }

    }) {
    var page = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowGroupViewHolder {
        return FollowGroupViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.cell_follow_list_recyclerview, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FollowGroupViewHolder, position: Int) {
        val obj = getItem(position)
        val adapter = FollowedUserAdapter(context)
        holder.swipeRefreshLayout.isRefreshing = true
        holder.recyclerView.layoutManager = LinearLayoutManager(context)
        holder.recyclerView.adapter = adapter
        holder.swipeRefreshLayout.setOnRefreshListener {
            page = 1
            UserManager.getFollowedUserByGroup(obj.tagid, page, object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    NetworkUtils.requireRetry {

                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val result =
                        Gson().fromJson(response.body?.string(), FollowGroupUsers::class.java)
                    MainScope().launch {
                        holder.swipeRefreshLayout.isRefreshing = false
                        adapter.submitList(result.data)
                        page++
                    }
                }

            })
        }
        holder.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                val lm = recyclerView.layoutManager as LinearLayoutManager?
                val totalItemCount = recyclerView.adapter!!.itemCount
                val lastVisibleItemPosition = lm!!.findLastVisibleItemPosition()
                val visibleItemCount = recyclerView.childCount
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemPosition == totalItemCount - 1 && visibleItemCount > 0) {
                    UserManager.getFollowedUserByGroup(obj.tagid, page, object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            NetworkUtils.requireRetry {

                            }
                        }

                        override fun onResponse(call: Call, response: Response) {
                            val result = Gson().fromJson(
                                response.body?.string(),
                                FollowGroupUsers::class.java
                            )
                            MainScope().launch {
                                holder.swipeRefreshLayout.isRefreshing = false
                                adapter.submitList(adapter.currentList + result.data)
                                page++
                            }
                        }

                    })
                }
            }
        })
        UserManager.getFollowedUserByGroup(obj.tagid, page, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                NetworkUtils.requireRetry {

                }
            }

            override fun onResponse(call: Call, response: Response) {
                val result = Gson().fromJson(response.body?.string(), FollowGroupUsers::class.java)
                MainScope().launch {
                    holder.swipeRefreshLayout.isRefreshing = false
                    adapter.submitList(adapter.currentList + result.data)
                    page++
                }
            }

        })
    }


    class FollowGroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerView: RecyclerView
        val swipeRefreshLayout: SwipeRefreshLayout

        init {
            recyclerView = itemView.findViewById(R.id.recyclerView)
            swipeRefreshLayout = itemView.findViewById(R.id.swipeRefreshLayout)
        }
    }
}