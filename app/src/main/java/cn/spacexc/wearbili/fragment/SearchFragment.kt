package cn.spacexc.wearbili.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.spacexc.wearbili.activity.SearchResultActivity
import cn.spacexc.wearbili.databinding.FragmentSearchBinding

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

    private fun searchKeyword() {
        if (binding.keywordInput.text.isNullOrBlank() || binding.keywordInput.text.contains("&") || binding.keywordInput.text.contains(
                "/"
            ) || binding.keywordInput.text.contains("?")
        ) return
        val keyword: String = binding.keywordInput.text.toString()
        val intent = Intent(requireActivity(), SearchResultActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("keyword", keyword)
        startActivity(intent)

    }
}
