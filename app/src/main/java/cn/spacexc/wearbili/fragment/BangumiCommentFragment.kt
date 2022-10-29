package cn.spacexc.wearbili.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.adapter.CommentAdapter
import cn.spacexc.wearbili.databinding.FragmentCommentBinding
import cn.spacexc.wearbili.dataclass.CommentContentData
import cn.spacexc.wearbili.utils.ToastUtils
import com.google.gson.Gson

class BangumiCommentFragment : Fragment() {

    private var _binding: FragmentCommentBinding? = null
    private val binding get() = _binding!!

    private val activityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult: ActivityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                when (activityResult.data?.getIntExtra("code", 0)) {
                    0 -> {
                        val comment = Gson().fromJson(
                            activityResult.data?.getStringExtra("commentDataStr"),
                            CommentContentData::class.java
                        )
                        val currentMutableList = adapter.currentList.toMutableList()
                        currentMutableList.removeAt(0)
                        val list = listOf(null, comment) + currentMutableList
                        adapter.submitList(list)
                        binding.recyclerView.smoothScrollToPosition(0)
                        ToastUtils.showText("发送成功")
                    }
                }

            }
        }

    var page: Int = 1

    lateinit var adapter: CommentAdapter
    //lateinit var layoutManager: WearableLinearLayoutManager

    init {
        Log.d(Application.getTag(), "CommentFragmentLoaded")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun refresh() {
        if (isAdded) {
            requireActivity().runOnUiThread {
                page = 1
                binding.recyclerView.smoothScrollToPosition(0)
                binding.swipeRefreshLayout.isRefreshing = true
                getComment(true)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    override fun onResume() {
        super.onResume()
        if (adapter.currentList.isEmpty()) {
            binding.swipeRefreshLayout.isRefreshing = true
            getComment(true)
        }
    }

    fun getComment(isRefresh: Boolean) {

    }
}