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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.activity.*
import cn.spacexc.wearbili.adapter.ButtonsAdapter
import cn.spacexc.wearbili.adapter.VideoPartsAdapter
import cn.spacexc.wearbili.databinding.FragmentVideoInfoBinding
import cn.spacexc.wearbili.dataclass.ButtonData
import cn.spacexc.wearbili.dataclass.VideoInfo
import cn.spacexc.wearbili.dataclass.VideoPages
import cn.spacexc.wearbili.manager.VideoManager
import cn.spacexc.wearbili.utils.NumberUtils
import cn.spacexc.wearbili.utils.TimeUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
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

    lateinit var videoPartsAdapter: VideoPartsAdapter

    private val btnListUpperRow = listOf(
        ButtonData(R.drawable.ic_outline_thumb_up_24, "点赞") {

        },
        ButtonData(R.drawable.ic_outline_thumb_down_24, "点踩") {

        },
        ButtonData(R.drawable.ic_outline_monetization_on_24, "投币") {

        },
        ButtonData(R.drawable.ic_round_star_border_24, "收藏") {

        },
        ButtonData(R.drawable.send_to_mobile, "手机观看") {
            if (isAdded) {
                val intent = Intent(requireActivity(), PlayOnPhoneActivity::class.java)
                intent.putExtra(
                    "qrCodeUrl",
                    "https://www.bilibili.com/video/${(activity as VideoActivity).getId()}"
                )
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                requireActivity().startActivity(intent)
            }
        },
        ButtonData(R.drawable.cloud_download, "缓存") {

        },
        ButtonData(R.drawable.ic_baseline_history_24, "稍后再看") {

        },
        ButtonData(R.drawable.ic_baseline_update_24, "历史记录") {

        }
    )

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
        binding.recyclerViewButtons.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerViewButtons.adapter =
            ButtonsAdapter(true).also { it.submitList(btnListUpperRow) }
        binding.recyclerViewParts.layoutManager = LinearLayoutManager(requireContext())
        videoPartsAdapter = VideoPartsAdapter((activity as VideoActivity).getId()!!)
        binding.recyclerViewParts.adapter = videoPartsAdapter
        //binding.recyclerViewLower.adapter = ButtonsAdapter(true).also { it.submitList(btnListLowerRow) }
        getVideo()
        getVideoParts()
    }

    private fun getVideoParts() {
        val id = (activity as VideoActivity).getId()
        VideoManager.getVideoParts(id, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                mThreadPool.execute {
                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            Application.getContext(),
                            "加载失败了",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!isAdded) return
                val responseStr = response.body?.string()
                val result = Gson().fromJson(responseStr, VideoPages::class.java)
                mThreadPool.execute {
                    requireActivity().runOnUiThread {
                        if (response.code == 200 && result.code == 0 && result.data.size != 1 or 0) {
                            videoPartsAdapter?.submitList(result.data.toList())
                            binding.videoPartsTitle.visibility = View.VISIBLE
                            binding.recyclerViewParts.visibility = View.VISIBLE
                            binding.videoPartsTitle.setOnClickListener {
                                val intent = Intent(
                                    requireActivity(),
                                    ViewFullVideoPartsActivity::class.java
                                )
                                intent.putExtra("data", responseStr)
                                intent.putExtra("bvid", (activity as VideoActivity).getId())
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                Application.getContext().startActivity(intent)
                            }
                        }
                    }
                }
            }

        })
    }

    private fun getVideo() {
        val id = (activity as VideoActivity).getId()
        VideoManager.getVideoById(id, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                mThreadPool.execute {
                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            Application.getContext(),
                            "加载失败了",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
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
                            binding.cover.setOnClickListener {
                                //(activity as VideoActivity).setPage(2)
                                val intent =
                                    Intent(requireActivity(), VideoPlayerActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                intent.putExtra("videoBvid", video.data.bvid)
                                intent.putExtra("videoCid", video.data.cid)
                                startActivity(intent)
                            }
                            binding.videoTitle.text = video.data.title
                            binding.bvidText.text = video.data.bvid
                            binding.duration.text = TimeUtils.secondToTime(video.data.duration)
                            binding.upNameText.text = video.data.owner.name
                            binding.danmakusCount.text =
                                NumberUtils.num2Chinese(video.data.stat.danmaku)
                            binding.viewsCount.text =
                                NumberUtils.num2Chinese(video.data.stat.view.toInt())
                            binding.videoDesc.text = video.data.desc

                            binding.bvidText.setOnLongClickListener {
                                val clipboardManager: ClipboardManager =
                                    ContextCompat.getSystemService(
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
                            val roundedCorners = RoundedCorners(10)
                            val options = RequestOptions.bitmapTransform(roundedCorners)
                            Glide.with(Application.getContext()).load(video.data.pic)
                                .placeholder(R.drawable.placeholder).apply(options)
                                .into(binding.cover)
                            //GlideUtils.loadPicsFitWidth(Application.getContext(), video.data.pic, R.drawable.placeholder, R.drawable.placeholder, binding.cover)
                        }else{
                            Toast.makeText(requireContext(), "加载失败了", Toast.LENGTH_SHORT).show()
                        }

                    }
                }

            }

        })
    }
}