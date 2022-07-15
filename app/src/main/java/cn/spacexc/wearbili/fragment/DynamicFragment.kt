package cn.spacexc.wearbili.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.adapter.CommentAdapter
import cn.spacexc.wearbili.databinding.FragmentCommentBinding
import cn.spacexc.wearbili.databinding.FragmentDynamicBinding
import cn.spacexc.wearbili.dataclass.CommentContentData
import cn.spacexc.wearbili.manager.DynamicManager
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class DynamicFragment : Fragment() {
    private var _binding: FragmentDynamicBinding? = null
    private val binding get() = _binding!!


    val mThreadPool: ExecutorService = Executors.newCachedThreadPool()

    var page : Int = 1

    val adapter = CommentAdapter()
    private val layoutManager = LinearLayoutManager(Application.getContext())

    var prevList : MutableList<CommentContentData>? = null


    init {
        Log.d(Application.getTag(), "CommentFragmentLoaded")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDynamicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DynamicManager.getRecommendDynamics()
    }
}