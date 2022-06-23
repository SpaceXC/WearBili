package cn.spacexc.wearbili.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.activity.SearchResultActivity
import cn.spacexc.wearbili.databinding.FragmentRecommendBinding
import cn.spacexc.wearbili.databinding.FragmentSearchBinding
import cn.spacexc.wearbili.manager.VideoManager
import cn.spacexc.wearbili.utils.NetworkUtils
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!


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
        binding.searchButton.setOnClickListener{searchKeyword()}
    }

    fun searchKeyword(){
        if(binding.keywordInput.text.isNullOrBlank() || binding.keywordInput.text.contains("&") || binding.keywordInput.text.contains("/") || binding.keywordInput.text.contains("?")) return
        val keyword : String = binding.keywordInput.text.toString()
        val intent = Intent(requireActivity(), SearchResultActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("keyword", keyword)
        startActivity(intent)

    }
}
