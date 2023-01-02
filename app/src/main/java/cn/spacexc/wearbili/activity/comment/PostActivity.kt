package cn.spacexc.wearbili.activity.comment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.spacexc.wearbili.databinding.ActivityPostBinding
import cn.spacexc.wearbili.dataclass.comment.CommentSent
import cn.spacexc.wearbili.manager.CommentManager
import cn.spacexc.wearbili.utils.NetworkUtils
import cn.spacexc.wearbili.utils.ToastUtils
import com.google.gson.Gson
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

const val COMMENT_TYPE = "commentAreaType"
const val COMMENT_TYPE_VIDEO = "videoComment"
const val COMMENT_TYPE_TEXT_DYNAMIC = "textDynamicComment"
const val COMMENT_TYPE_IMAGE_DYNAMIC = "imageDynamicComment"

class PostActivity : AppCompatActivity() {
    lateinit var binding: ActivityPostBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        lifecycleScope.launch {
//            while (true){
//                binding.timeText.text = TimeUtils.getCurrentTime()
//                delay(500)
//            }
//        }
        binding.pageName.setOnClickListener { finish() }
        binding.enter.setOnClickListener {
            binding.input.setText("${binding.input.text}\n")
            binding.input.setSelection(binding.input.length())
        }
        binding.send.setOnClickListener {
            binding.send.isEnabled = false
            binding.emoji.isEnabled = false
            binding.enter.isEnabled = false
            binding.input.isEnabled = false
            sendComment()
        }
    }

    private fun sendComment(){
        val type = intent.getStringExtra(COMMENT_TYPE)
        val oid = intent.getLongExtra("oid", 0)
        val content = binding.input.text?.toString()

        if (content.isNullOrEmpty()) {
            ToastUtils.showText("发些有意义的东西叭")
            return
        }

        CommentManager.sendComment(
            type!!,
            oid,
            content,
            object : NetworkUtils.ResultCallback<CommentSent> {
                override fun onSuccess(result: CommentSent, code: Int) {
                    setResult(
                        Activity.RESULT_OK,
                        Intent().putExtra("commentDataStr", Gson().toJson(result.data.reply))
                            .putExtra("code", result.code)
                    )
                    finish()
                }

                override fun onFailed(e: Exception?) {
                    MainScope().launch {
                        ToastUtils.showText("网络异常")
                        binding.send.isEnabled = true
                        binding.emoji.isEnabled = true
                        binding.enter.isEnabled = true
                        binding.input.isEnabled = true
                    }
                }

        })
    }
}