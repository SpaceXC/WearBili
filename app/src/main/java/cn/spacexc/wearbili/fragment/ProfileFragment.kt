package cn.spacexc.wearbili.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.activity.settings.SettingsActivity
import cn.spacexc.wearbili.activity.user.*
import cn.spacexc.wearbili.activity.video.VideoCacheActivity
import cn.spacexc.wearbili.activity.video.WatchLaterActivity
import cn.spacexc.wearbili.adapter.ButtonsAdapter
import cn.spacexc.wearbili.databinding.FragmentProfileBinding
import cn.spacexc.wearbili.dataclass.RoundButtonData
import cn.spacexc.wearbili.dataclass.SpaceProfileResult
import cn.spacexc.wearbili.listener.OnItemViewClickListener
import cn.spacexc.wearbili.manager.UserManager
import cn.spacexc.wearbili.utils.NumberUtils.toShortChinese
import cn.spacexc.wearbili.utils.ToastUtils
import cn.spacexc.wearbili.utils.ViewUtils.addClickScale
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException


class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!


    //个人页按钮
    private val buttonList = listOf(
        RoundButtonData(R.drawable.ic_outline_person_add_alt_1_24, "我的关注", "我的关注"),
        RoundButtonData(R.drawable.ic_baseline_update_24, "历史记录", "历史记录"),
        RoundButtonData(R.drawable.ic_baseline_play_circle_outline_24, "稍后再看", "稍后再看"),
        RoundButtonData(R.drawable.ic_round_star_border_24, "个人收藏", "个人收藏"),
        RoundButtonData(R.drawable.chat_bubble, "我的消息", "我的消息"),
        RoundButtonData(R.drawable.cloud_download, "离线缓存", "离线缓存"),
        //RoundButtonData(R.drawable.ic_outline_settings_24, "应用设置", "应用设置")
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
                override fun onClick(buttonName: String, viewHolder: RecyclerView.ViewHolder) {
                    when (buttonName) {
                        "历史记录" -> {
                            if (isAdded) {
                                val intent =
                                    Intent(requireContext(), HistoryActivity::class.java)
                                startActivity(intent)
                            }
                        }
                        "我的关注" -> {
                            if (isAdded) {
                                val intent =
                                    Intent(requireContext(), FollowListActivity::class.java)
                                startActivity(intent)
                            }
                        }
                        "离线缓存" -> {
                            if (isAdded) {
                                val intent =
                                    Intent(requireContext(), VideoCacheActivity::class.java)
                                startActivity(intent)
                            }
                        }
                        "稍后再看" -> {
                            if (isAdded) {
                                val intent =
                                    Intent(requireContext(), WatchLaterActivity::class.java)
                                startActivity(intent)
                            }
                        }
                        "个人收藏" -> {
                            if (isAdded) {
                                val intent =
                                    Intent(requireContext(), StaredActivity::class.java)
                                startActivity(intent)
                            }
                        }
                    }
                }

                override fun onLongClick(buttonName: String, viewHolder: RecyclerView.ViewHolder) {

                }

            }).also {
                it.submitList(buttonList)
            }
        refreshLogin()
        binding.settings.setOnClickListener {
            if (isAdded) {
                val intent =
                    Intent(requireContext(), SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        binding.defAvatar.setOnClickListener { refreshLogin() }
        binding.avatar.setOnClickListener { refreshLogin() }
        binding.login.setOnClickListener {
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.putExtra("fromHome", false)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
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
            binding.apply {
                defAvatar.isVisible = true
                guest.isVisible = true
                loginHint.isVisible = true
                login.isVisible = true

                avatarGroup.isVisible = false
                accountSurvey.isVisible = false
                nicknameGroup.isVisible = false
                littleArrow.isVisible = false
                buttonGroup.isVisible = false
                settings.isVisible = false
            }

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
                    MainScope().launch {
                        ToastUtils.makeText(
                            "获取用户信息失败，点击头像重试"
                        ).show()
                        binding.guest.isVisible = true
                        binding.defAvatar.isVisible = true
                        binding.guest.text = "加载失败"
                        binding.defAvatar.isEnabled = true

                    }
                }

                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call, response: Response) {
                    MainScope().launch {
                        val user: SpaceProfileResult = Gson().fromJson(
                            response.body?.string(),
                            SpaceProfileResult::class.java
                        )
                        if (user.code == -101) {
                            binding.apply {
                                defAvatar.isVisible = true
                                guest.isVisible = true
                                loginHint.isVisible = true
                                login.isVisible = true
                                guest.text = "访客"

                                avatarGroup.isVisible = false
                                accountSurvey.isVisible = false
                                nicknameGroup.isVisible = false
                                littleArrow.isVisible = false
                                buttonGroup.isVisible = false
                                settings.isVisible = false
                            }
                            return@launch
                        }
                        binding.apply {
                            defAvatar.isVisible = false
                            guest.isVisible = false
                            loginHint.isVisible = false
                            login.isVisible = false
                            guest.text = "访客"

                            avatarGroup.isVisible = true
                            accountSurvey.isVisible = true
                            nicknameGroup.isVisible = true
                            littleArrow.isVisible = true
                            buttonGroup.isVisible = true
                            settings.isVisible = true
                        }
                        Glide.with(Application.getContext()).load(user.data.face)
                            .placeholder(R.drawable.default_avatar).circleCrop()
                            .into(binding.avatar)
                        //user.data.pendant.image_enhance = ""
                        if (!user.data.pendant.image_enhance.isNullOrEmpty()) {
                            Glide.with(Application.getContext())
                                .load(user.data.pendant.image_enhance)
                                .placeholder(R.drawable.empty_placeholder)
                                .into(binding.pendant)
                        } else {
                            binding.pendant.setImageResource(R.drawable.empty_placeholder)
                        }
                        binding.usernameText.text = user.data.name
                        binding.usernameText.addClickScale()
                        binding.usernameText.setOnClickListener {
                            Intent(requireContext(), SpaceProfileActivity::class.java).apply {
                                putExtra("userMid", user.data.mid)
                                startActivity(this)
                            }
                        }
                        binding.fansText.text = user.data.follower.toShortChinese()
                        binding.coinsText.text = user.data.coins.toString()
                        //binding.uidText.text = "UID ${user.data.mid}"
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

            })
        }
    }
}



