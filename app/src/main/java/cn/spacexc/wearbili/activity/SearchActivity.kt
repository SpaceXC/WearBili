package cn.spacexc.wearbili.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.adapter.HotSearchAdapter
import cn.spacexc.wearbili.databinding.ActivitySearchBinding
import cn.spacexc.wearbili.dataclass.DefaultSearchContent
import cn.spacexc.wearbili.dataclass.HotSearch
import cn.spacexc.wearbili.manager.SearchManager
import cn.spacexc.wearbili.utils.TimeUtils
import cn.spacexc.wearbili.utils.ToastUtils
import cn.spacexc.wearbili.utils.VideoUtils
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.pow

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding

    val adapter: HotSearchAdapter = HotSearchAdapter()


    val mThreadPool: ExecutorService = Executors.newCachedThreadPool()

    var defaultContent = ""
    var defaultType: Int? = null
    var defaultVideoAv: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycleScope.launch {
            while (true) {
                binding.timeText.text = TimeUtils.getCurrentTime()
                delay(500)
            }
        }
        binding.pageName.setOnClickListener { finish() }
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
//        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                val lm = recyclerView.layoutManager as LinearLayoutManager?
//                val totalItemCount = recyclerView.adapter!!.itemCount
//                val lastVisibleItemPosition = lm!!.findLastVisibleItemPosition()
//                val visibleItemCount = recyclerView.childCount
//                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemPosition == totalItemCount - 1 && visibleItemCount > 0) {
//                    searchKeyword(binding.keywordInput.text.toString(), false)
//                }
//            }
//        })
//        binding.keywordInput.addTextChangedListener(object : TextWatcher{
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) =
//                if(p0?.isNotEmpty() == true) {
//                    binding.recyclerView.smoothScrollToPosition(0)
//                    currentPage = 1
//                    searchKeyword(p0.toString(), true)
//                }
//                else{
//                    adapter.submitList(emptyList())
//                }
//
//            override fun afterTextChanged(p0: Editable?) {
//
//            }
//
//        })
    }

    private fun getDefaultSearchContent() {
        SearchManager.getDefaultSearchContent(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                mThreadPool.execute {
                    this@SearchActivity.runOnUiThread {
                        ToastUtils.makeText(
                            "热搜获取失败"
                        ).show()
                    }

                }
            }

            override fun onResponse(call: Call, response: Response) {
                mThreadPool.execute {
                    this@SearchActivity.runOnUiThread {
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

                mThreadPool.execute {
                    this@SearchActivity.runOnUiThread {
                        ToastUtils.makeText(
                            "热搜获取失败"
                        ).show()
                    }
                }


            }

            override fun onResponse(call: Call, response: Response) {

                mThreadPool.execute {
                    this@SearchActivity.runOnUiThread {
                        val result =
                            Gson().fromJson(response.body?.string(), HotSearch::class.java)
                        adapter.submitList(result.list)
                    }
                }

            }

        })
    }

    private fun searchKeyword() {
        val text = binding.keywordInput.text
        if (text.contains("自杀")) {
            val intent = Intent(this, SpecialSearchActivity::class.java)
            startActivity(intent)
            return
        }
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
                    val intent = Intent(this@SearchActivity, SearchResultActivity::class.java)
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
            val intent = Intent(this@SearchActivity, SearchResultActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("keyword", keyword)
            startActivity(intent)
        }
    }

//    fun searchKeyword(keyword : String, isNew: Boolean) {
//        VideoManager.searchVideo(keyword, currentPage, object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                mThreadPool.execute {
//                    requireActivity().runOnUiThread {
//                        ToastUtils.makeText(requireActivity(), "搜索失败了", Toast.LENGTH_SHORT)
//                            .show()
//                    }
//                }
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                val result = Gson().fromJson(response.body?.string(), VideoSearch::class.java)
//                mThreadPool.execute {
//                    requireActivity().runOnUiThread {
//                        //binding.pageName.text = "搜索结果 (${result.data.numResults})"
//                        if (result.code == 0) {
//                            if (currentPage <= 50) {
//                                if(isNew) {
//                                    adapter.submitList(result.data.result)
//                                }
//                                else{
//                                    if(result.data.result != null){
//                                        adapter.submitList(adapter.currentList + result.data.result!!)
//                                    }
//
//                                }
//
//                                binding.swipeRefreshLayout.isRefreshing = false
//                                currentPage++
//
//                            } else {
//                                binding.swipeRefreshLayout.isRefreshing = false
//                                ToastUtils.makeText(
//                                    requireActivity(),
//                                    "搜索到底了",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//
//                            }
//                        } else {
//                            binding.swipeRefreshLayout.isRefreshing = false
//                            ToastUtils.makeText(requireActivity(), "搜索失败了", Toast.LENGTH_SHORT)
//                                .show()
//                        }
//                    }
//                }
//
//            }
//
//
//        })
//    }

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