package cn.spacexc.wearbili.activity.search

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.activity.user.SpaceProfileActivity
import cn.spacexc.wearbili.activity.video.VideoActivity
import cn.spacexc.wearbili.adapter.HotSearchAdapter
import cn.spacexc.wearbili.databinding.ActivitySearchBinding
import cn.spacexc.wearbili.dataclass.DefaultSearchContent
import cn.spacexc.wearbili.dataclass.HotSearch
import cn.spacexc.wearbili.manager.SearchManager
import cn.spacexc.wearbili.utils.ToastUtils
import cn.spacexc.wearbili.utils.VideoUtils
import cn.spacexc.wearbili.utils.VideoUtils.isAV
import cn.spacexc.wearbili.utils.VideoUtils.isBV
import com.google.gson.Gson
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding

    val adapter: HotSearchAdapter = HotSearchAdapter()


    var defaultContent = ""
    var defaultType: Int? = null
    var defaultVideoAv: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        lifecycleScope.launch {
//            while (true) {
//                binding.timeText.text = TimeUtils.getCurrentTime()
//                delay(500)
//            }
//        }
        binding.pageName.setOnClickListener { finish() }
        binding.hotSearchRecyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(this)
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
                        "热搜获取失败"
                    ).show()

                }
            }

            override fun onResponse(call: Call, response: Response) {
                MainScope().launch {
                    try {
                        val result =
                            Gson().fromJson(
                                response.body?.string(),
                                DefaultSearchContent::class.java
                            )
                        binding.keywordInput.hint = result.data.show_name
                        defaultContent = result.data.show_name
                        defaultType = result.data.goto_type
                        defaultVideoAv = result.data.goto_value
                    } catch (_: Exception) {
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
                    val result =
                        Gson().fromJson(response.body?.string(), HotSearch::class.java)
                    adapter.submitList(result.list)

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
                    val intent = Intent(this@SearchActivity, SearchResultActivityNew::class.java)
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
        } else if (keyword.startsWith("uid") || keyword.startsWith("mid")) {
            val uid = keyword.substring(3, keyword.length)
            if (uid.isNotEmpty()) {
                Intent(this, SpaceProfileActivity::class.java).apply {
                    putExtra("userMid", uid.toLong())
                    startActivity(this)
                }
            } else {
                val intent = Intent(this@SearchActivity, SearchResultActivityNew::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra("keyword", keyword)
                startActivity(intent)
            }
        } else {
            val intent = Intent(this@SearchActivity, SearchResultActivityNew::class.java)
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


}