package cn.spacexc.wearbili.fragment

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.activity.PhotoViewActivity
import cn.spacexc.wearbili.activity.VideoActivity
import cn.spacexc.wearbili.databinding.FragmentVideoInfoBinding
import cn.spacexc.wearbili.dataclass.VideoInfo
import cn.spacexc.wearbili.manager.VideoManager
import cn.spacexc.wearbili.utils.GlideUtils
import cn.spacexc.wearbili.utils.TimeUtils
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class VideoInfoFragment : Fragment() {
    private var _binding: FragmentVideoInfoBinding? = null
    private val binding get() = _binding!!

    val mThreadPool: ExecutorService = Executors.newCachedThreadPool()

    init {
        Log.d(Application.getTag(), "VideoInfoFragmentLoaded")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getVideo()
    }

    private fun getVideo() {
        val id = (activity as VideoActivity).getId()
        VideoManager.getVideoById(id, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Toast.makeText(Application.getContext(), "加载失败了", Toast.LENGTH_SHORT).show()
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call, response: Response) {
                val video = Gson().fromJson(response.body?.string(), VideoInfo::class.java)
                mThreadPool.execute {
                    requireActivity().runOnUiThread{
                        if(response.code == 200 && video.code == 0){
                            (activity as VideoActivity).currentVideo = video.data
                            (activity as VideoActivity).isInitialized = true
                            binding.cover.setOnLongClickListener {
                                val intent = Intent(requireActivity(), PhotoViewActivity::class.java)
                                intent.putExtra("imageUrl", video.data.pic)
                                startActivity(intent)
                                true
                            }
                            binding.cover.setOnClickListener{
                                (activity as VideoActivity).setPage(2)
                            }
                            binding.videoTitle.text = video.data.title
                            binding.bvid.text = video.data.bvid
                            binding.duration.text = TimeUtils.secondToTime(video.data.duration)
                            binding.upName.text = "UP: ${video.data.owner.name}"
                            binding.videoSurvey.text = "${video.data.stat.danmaku}弹幕  ${video.data.stat.view}播放"
                            binding.videoDesc.text = video.data.desc

                            binding.bvid.setOnLongClickListener {
                                val clipboardManager: ClipboardManager = ContextCompat.getSystemService(
                                    requireContext(),
                                    ClipboardManager::class.java
                                ) as ClipboardManager
                                val clip: ClipData =
                                    ClipData.newPlainText("wearbili bvid", video.data.bvid)
                                clipboardManager.setPrimaryClip(clip)
                                Toast.makeText(requireContext(), "已复制BV号", Toast.LENGTH_SHORT)
                                    .show()
                                true
                            }

                            binding.videoDesc.setOnLongClickListener {
                                val clipboardManager: ClipboardManager = ContextCompat.getSystemService(
                                    requireContext(),
                                    ClipboardManager::class.java
                                ) as ClipboardManager
                                val clip: ClipData =
                                    ClipData.newPlainText("wearbili desc", video.data.desc)
                                clipboardManager.setPrimaryClip(clip)
                                Toast.makeText(requireContext(), "已复制简介", Toast.LENGTH_SHORT)
                                    .show()
                                true
                            }
                            GlideUtils.loadPicsFitWidth(Application.getContext(), video.data.pic, R.drawable.placeholder, R.drawable.placeholder, binding.cover)
                        }else{
                            Toast.makeText(requireContext(), "加载失败了", Toast.LENGTH_SHORT).show()
                        }

                    }
                }

            }

        })
    }
}