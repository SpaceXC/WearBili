package cn.spacexc.wearbili.fragment

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.activity.LoginActivity
import cn.spacexc.wearbili.databinding.FragmentProfileBinding
import cn.spacexc.wearbili.dataclass.SpaceProfileResult
import cn.spacexc.wearbili.manager.UserManager
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshLogin()
        binding.avatar.setOnClickListener{refreshLogin()}
    }

    private fun refreshLogin() {
        Log.d(Application.getTag(), "refreshLogin: ")
        Glide
            .with(this)
            .load(R.drawable.akari)
            .circleCrop()
            .into(binding.avatar)
        binding.usernameText.text = "加载中..."
        binding.avatar.isEnabled = false
        if(UserManager.getUserCookie() == null){
            binding.usernameText.text = "还没登录吖"
            binding.login.visibility = View.VISIBLE
            binding.login.setOnClickListener {
                startActivity(
                    Intent(requireActivity(),
                        LoginActivity::class.java
                    )
                )
            }
        }
        else{
            UserManager.getCurrentUser(object : Callback{
                override fun onFailure(call: Call, e: IOException) {
                    mThreadPool.execute{
                        requireActivity().runOnUiThread{
                            Toast.makeText(requireContext(), "获取用户信息失败，点击头像重试", Toast.LENGTH_SHORT).show()
                            binding.usernameText.text = "加载失败"
                            binding.avatar.isEnabled = true
                        }
                    }
                }

                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call, response: Response) {
                    mThreadPool.execute{
                        requireActivity().runOnUiThread{
                            val user : SpaceProfileResult = Gson().fromJson(response.body?.string(), SpaceProfileResult::class.java)
                            Glide.with(requireActivity()).load(Uri.parse(user.data.face)).circleCrop().into(binding.avatar)
                            binding.usernameText.text = user.data.name
                            binding.survey.text = "硬币: ${user.data.coins}  粉丝: ${user.data.follower}"
                            //binding.uidText.text = "UID ${user.data.mid}"
                            //binding.signText.text = user.data.sign.ifEmpty { "这个人什么都没写..." }
                            binding.levelText.text = "LV${user.data.level}"
                            binding.levelText.visibility = View.VISIBLE
                            if(user.data.vip.type != 0) {
                                //binding.vipText.text = user.data.vip.label.text
                                binding.usernameText.setTextColor(Color.parseColor(user.data.vip.nickname_color))
                                //binding.vipText.setTextColor(Color.parseColor(user.data.vip.label.bg_color))
                            }
                            binding.login.visibility = View.INVISIBLE
                            binding.avatar.isEnabled = true

                            /*binding.uidText.setOnLongClickListener {
                                var clipboardManager: ClipboardManager = getSystemService(
                                    requireContext(),
                                    ClipboardManager::class.java
                                ) as ClipboardManager
                                val clip: ClipData =
                                    ClipData.newPlainText("wearbili uid", user.data.mid.toString())
                                clipboardManager.setPrimaryClip(clip)
                                Toast.makeText(requireContext(), "已复制UID~", Toast.LENGTH_SHORT)
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