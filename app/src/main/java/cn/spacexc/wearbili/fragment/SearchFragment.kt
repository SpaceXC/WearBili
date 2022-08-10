package cn.spacexc.wearbili.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.activity.search.SearchResultActivity
import cn.spacexc.wearbili.activity.video.VideoActivity
import cn.spacexc.wearbili.adapter.HotSearchAdapter
import cn.spacexc.wearbili.databinding.ActivitySearchBinding
import cn.spacexc.wearbili.dataclass.DefaultSearchContent
import cn.spacexc.wearbili.dataclass.HotSearch
import cn.spacexc.wearbili.manager.SearchManager
import cn.spacexc.wearbili.utils.ToastUtils
import cn.spacexc.wearbili.utils.VideoUtils
import com.google.gson.Gson
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.pow


class SearchFragment : Fragment() {
    private var _binding: ActivitySearchBinding? = null
    private val binding get() = _binding!!

    val adapter: HotSearchAdapter = HotSearchAdapter()


    val mThreadPool: ExecutorService = Executors.newCachedThreadPool()

    var currentPage: Int = 1

    var defaultContent = ""
    var defaultType: Int? = null
    var defaultVideoAv: String? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivitySearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //binding.searchButton.setOnClickListener { searchKeyword() }
        binding.hotSearchRecyclerView.adapter = adapter
        val layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL)
        binding.hotSearchRecyclerView.layoutManager = layoutManager
        binding.search.setOnClickListener {
            searchKeyword()
        }

        binding.keywordInput.setOnEditorActionListener { _, actionId, event -> //当actionId == XX_SEND 或者 XX_DONE时都触发
            //或者event.getKeyCode == ENTER 且 event.getAction == ACTION_DOWN时也触发
            //注意，这是一定要判断event != null。因为在某些输入法上会返回null。
            if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE || event != null && KeyEvent.KEYCODE_ENTER == event.keyCode && KeyEvent.ACTION_DOWN == event.action) {
                searchKeyword()
            }
            false
        }
        getDefaultSearchContent()
        getHotSearch()
    }

    private fun getDefaultSearchContent() {
        SearchManager.getDefaultSearchContent(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                MainScope().launch {
                    ToastUtils.makeText(
                        "网络异常   "
                    ).show()
                }


            }

            override fun onResponse(call: Call, response: Response) {
                MainScope().launch {
                    kotlin.runCatching {
                        val result =
                            Gson().fromJson(
                                response.body?.string(),
                                DefaultSearchContent::class.java
                            )
                        binding.keywordInput.hint = result.data.show_name
                        defaultContent = result.data.show_name
                        defaultType = result.data.goto_type
                        defaultVideoAv = result.data.goto_value
                    }

                }
            }

        })
    }

    private fun getHotSearch() {
        SearchManager.getHotSearch(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                MainScope().launch {
                    ToastUtils.makeText(
                        "热搜获取失败"
                    ).show()
                }


            }

            override fun onResponse(call: Call, response: Response) {
                MainScope().launch {
                    kotlin.runCatching {
                        val result =
                            Gson().fromJson(response.body?.string(), HotSearch::class.java)
                        adapter.submitList(result.list)
                    }

                }
            }
        })
    }

    private fun searchKeyword() {
        if (binding.keywordInput.text.contains("&") || binding.keywordInput.text.contains(
                "/"
            ) || binding.keywordInput.text.contains("?")
        ) return

        if (binding.keywordInput.text.isNullOrBlank()) {
            if (defaultType != null && defaultContent.isNotEmpty()) {
                if (defaultType == 1 && !defaultVideoAv.isNullOrBlank()) {
                    val bv = VideoUtils.av2bv("av$defaultVideoAv")
                    val intent = Intent(Application.getContext(), VideoActivity::class.java)
                    intent.putExtra("videoId", bv)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    return
                } else {
                    val intent = Intent(requireActivity(), SearchResultActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.putExtra("keyword", defaultContent)
                    startActivity(intent)
                    return
                }
            }
        }
        val keyword: String = binding.keywordInput.text.toString()

        if (isAV(keyword)) {
            val bv = VideoUtils.av2bv(keyword)
            val intent = Intent(Application.getContext(), VideoActivity::class.java)
            intent.putExtra("videoId", bv)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        } else if (isBV(keyword)) {
            val intent = Intent(Application.getContext(), VideoActivity::class.java)
            intent.putExtra("videoId", keyword)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        } else {
            val intent = Intent(requireActivity(), SearchResultActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("keyword", keyword)
            startActivity(intent)
        }
    }

    private fun isAV(av: String): Boolean {
        if (!av.startsWith("av")) return false
        try {
            val avstr = av.substring(2, av.length)
            Log.d(Application.getTag(), "isAV: $avstr")
            return if (av.isEmpty()) {
                false
            } else {
                try {
                    val avn1 = avstr.toLong()
                    //av号是绝对不可能大于2的32次方的，不然此算法也将作废
                    return avn1 < 2.0.pow(32.0)
                } catch (e: NumberFormatException) {
                    return false
                }

            }
        } catch (e: IndexOutOfBoundsException) {
            return false
        }

    }

    fun isBV(bv: String): Boolean {   //bv号转换av号
        if (bv.startsWith("BV")) { //先看看你有没有把bv带进来
            return if (bv.length != 12) { //判断长度
                false
            } else {
                val bv7 = bv[9]
                //判断bv号的格式是否正确，防止你瞎输
                if (bv.indexOf("1") == 2 && bv7 == '7') try {
                    true
                } catch (e: Exception) {
                    false
                } else { //如果格式不对的话就揍你一顿
                    false
                }
            }
        } else { //如果你不是用bv开头的
            return if (bv.length != 10) { //判断长度
                false
            } else {
                //判断格式是否正确
                bv.indexOf("1") == 0 && bv[7] == '7'
            }
        }
    }
}
