package cn.spacexc.wearbili.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.activity.LoginActivity
import cn.spacexc.wearbili.activity.VideoCacheActivity
import cn.spacexc.wearbili.activity.WatchLaterActivity
import cn.spacexc.wearbili.adapter.ButtonsAdapter
import cn.spacexc.wearbili.databinding.FragmentProfileBinding
import cn.spacexc.wearbili.dataclass.RoundButtonData
import cn.spacexc.wearbili.dataclass.SpaceProfileResult
import cn.spacexc.wearbili.listener.OnItemViewClickListener
import cn.spacexc.wearbili.manager.UserManager
import cn.spacexc.wearbili.utils.NumberUtils.toShortChinese
import cn.spacexc.wearbili.utils.ToastUtils
import com.bumptech.glide.Glide
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    val mThreadPool: ExecutorService = Executors.newCachedThreadPool()

    //TODO 个人页按钮
    private val buttonList = listOf(
        RoundButtonData(R.drawable.ic_outline_person_add_alt_1_24, "我的关注", "我的关注"),
        RoundButtonData(R.drawable.ic_baseline_update_24, "历史记录", "历史记录"),
        RoundButtonData(R.drawable.ic_baseline_play_circle_outline_24, "稍后再看", "稍后再看"),
        RoundButtonData(R.drawable.ic_round_star_border_24, "个人收藏", "个人收藏"),
        RoundButtonData(R.drawable.cloud_download, "离线缓存", "离线缓存"),
        RoundButtonData(R.drawable.ic_outline_settings_24, "应用设置", "应用设置")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    /*override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2).also {
            it.orientation = GridLayoutManager.VERTICAL
        }
        binding.recyclerView.adapter =
            ButtonsAdapter(false, object : OnItemViewClickListener {
                override fun onClick(buttonName: String) {
                    when (buttonName) {
                        "离线缓存" -> {
                            if (isAdded) {
                                val intent =
                                    Intent(requireContext(), VideoCacheActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                            }
                        }
                        "稍后再看" -> {
                            if (isAdded) {
                                val intent =
                                    Intent(requireContext(), WatchLaterActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                            }
                        }
                    }
                }

            }).also {
                it.submitList(buttonList)
            }
        refreshLogin()
        binding.avatar.setOnClickListener { refreshLogin() }
    }

    private fun refreshLogin() {
        Log.d(Application.getTag(), "refreshLogin: ")
        Glide
            .with(this)
            .load(R.drawable.default_avatar)
            .circleCrop()
            .into(binding.avatar)
        binding.usernameText.text = "加载中..."
        binding.avatar.isEnabled = false
        if (UserManager.getUserCookie() == null) {
            binding.usernameText.text = "访客"
            binding.textView14.visibility = View.VISIBLE
            binding.login.visibility = View.VISIBLE

            binding.login.setOnClickListener {
                startActivity(
                    Intent(
                        requireActivity(),
                        LoginActivity::class.java
                    )
                )
            }
        } else {
            UserManager.getCurrentUser(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    mThreadPool.execute {
                        requireActivity().runOnUiThread {
                            ToastUtils.makeText(
                                "获取用户信息失败，点击头像重试"
                            )
                                .show()
                            binding.usernameText.text = "加载失败"
                            binding.avatar.isEnabled = true
                        }
                    }
                }

                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call, response: Response) {
                    mThreadPool.execute {
                        requireActivity().runOnUiThread {
                            val user: SpaceProfileResult = Gson().fromJson(
                                response.body?.string(),
                                SpaceProfileResult::class.java
                            )
                            binding.also {
                                it.accountSurvey.visibility = View.VISIBLE
                                it.imageView19.visibility = View.VISIBLE
                                it.relativeLayout5.visibility = View.VISIBLE
                                it.settings.visibility = View.VISIBLE
                            }
                            Glide.with(requireActivity()).load(Uri.parse(user.data.face))
                                .placeholder(R.drawable.default_avatar).circleCrop()
                                .into(binding.avatar)
                            binding.usernameText.text = user.data.name
                            binding.fansText.text = user.data.follower.toShortChinese()
                            binding.coinsText.text = user.data.coins.toString()
                            //binding.uidText.text = "UID ${user.data.mid}"
                            //binding.signText.text = user.data.sign.ifEmpty { "这个人什么都没写..." }
                            binding.levelText.text = "LV${user.data.level}"
                            binding.levelText.visibility = View.VISIBLE
                            if (user.data.vip.nickname_color.isNotEmpty()) {
                                //binding.vipText.text = user.data.vip.label.text
                                binding.usernameText.setTextColor(Color.parseColor(user.data.vip.nickname_color))
                                //binding.vipText.setTextColor(Color.parseColor(user.data.vip.label.bg_color))
                            }
                            //binding.login.visibility = View.INVISIBLE
                            binding.avatar.isEnabled = true

                            /*binding.uidText.setOnLongClickListener {
                                var clipboardManager: ClipboardManager = getSystemService(
                                    requireContext(),
                                    ClipboardManager::class.java
                                ) as ClipboardManager
                                val clip: ClipData =
                                    ClipData.newPlainText("wearbili uid", user.data.mid.toString())
                                clipboardManager.setPrimaryClip(clip)
                                ToastUtils.makeText(requireContext(), "已复制UID~", Toast.LENGTH_SHORT)
                                    .show()
                                true
                            }*/
                        }
                    }
                }

            })
        }
    }
}



