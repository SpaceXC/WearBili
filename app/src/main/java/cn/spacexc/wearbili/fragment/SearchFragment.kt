package cn.spacexc.wearbili.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.activity.SearchResultActivity
import cn.spacexc.wearbili.activity.VideoActivity
import cn.spacexc.wearbili.adapter.HotSearchAdapter
import cn.spacexc.wearbili.databinding.FragmentSearchBinding
import cn.spacexc.wearbili.dataclass.HotSearch
import cn.spacexc.wearbili.manager.SearchManager
import cn.spacexc.wearbili.utils.VideoUtils
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.pow


class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    val adapter: HotSearchAdapter = HotSearchAdapter()


    val mThreadPool: ExecutorService = Executors.newCachedThreadPool()

    var currentPage: Int = 1


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
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

    private fun getHotSearch() {
        SearchManager.getHotSearch(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (isAdded) {
                    mThreadPool.execute {
                        requireActivity().runOnUiThread {
                            Toast.makeText(
                                requireContext(),
                                "热搜获取失败",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

            }

            override fun onResponse(call: Call, response: Response) {
                if (isAdded) {
                    mThreadPool.execute {
                        requireActivity().runOnUiThread {
                            val result =
                                Gson().fromJson(response.body?.string(), HotSearch::class.java)
                            adapter.submitList(result.list)
                        }
                    }
                }
            }

        })
    }

    private fun searchKeyword() {
        if (binding.keywordInput.text.isNullOrBlank() || binding.keywordInput.text.contains("&") || binding.keywordInput.text.contains(
                "/"
            ) || binding.keywordInput.text.contains("?")
        ) return
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

//    fun searchKeyword(keyword : String, isNew: Boolean) {
//        VideoManager.searchVideo(keyword, currentPage, object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                mThreadPool.execute {
//                    requireActivity().runOnUiThread {
//                        Toast.makeText(requireActivity(), "搜索失败了", Toast.LENGTH_SHORT)
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
//                                Toast.makeText(
//                                    requireActivity(),
//                                    "搜索到底了",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//
//                            }
//                        } else {
//                            binding.swipeRefreshLayout.isRefreshing = false
//                            Toast.makeText(requireActivity(), "搜索失败了", Toast.LENGTH_SHORT)
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

    private fun isAV(av: String): Boolean {   //av号转换bv号
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
