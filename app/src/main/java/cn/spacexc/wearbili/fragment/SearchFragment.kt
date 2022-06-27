package cn.spacexc.wearbili.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.spacexc.wearbili.Application
import cn.spacexc.wearbili.activity.SearchResultActivity
import cn.spacexc.wearbili.activity.VideoActivity
import cn.spacexc.wearbili.databinding.FragmentSearchBinding
import cn.spacexc.wearbili.utils.VideoUtils
import kotlin.math.pow


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
        binding.searchButton.setOnClickListener { searchKeyword() }
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
