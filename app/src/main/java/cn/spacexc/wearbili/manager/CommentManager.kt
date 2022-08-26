package cn.spacexc.wearbili.manager

import cn.spacexc.wearbili.activity.comment.COMMENT_TYPE_IMAGE_DYNAMIC
import cn.spacexc.wearbili.activity.comment.COMMENT_TYPE_TEXT_DYNAMIC
import cn.spacexc.wearbili.activity.comment.COMMENT_TYPE_VIDEO
import cn.spacexc.wearbili.dataclass.comment.CommentSent
import cn.spacexc.wearbili.utils.NetworkUtils
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.Response
import java.io.IOException

/**
 * Created by XC-Qan on 2022/8/25.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

object CommentManager {
    val commentTypes = mapOf(
        COMMENT_TYPE_VIDEO to 1,
        COMMENT_TYPE_IMAGE_DYNAMIC to 11,
        COMMENT_TYPE_TEXT_DYNAMIC to 17
    )

    fun sendComment(
        commentType: String,
        oid: Long,
        content: String,
        callback: NetworkUtils.ResultCallback<CommentSent>
    ) {
        val body = FormBody.Builder()
            .add("access_key", UserManager.getAccessKey()!!)
            .add("type", commentTypes[commentType]?.toString()!!)
            .add("oid", oid.toString())
            .add("message", content)
            .add("plat", "2")
            .build()
        NetworkUtils.postUrl("http://api.bilibili.com/x/v2/reply/add", body, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onFailed(e)
            }

            override fun onResponse(call: Call, response: Response) {
                val result = Gson().fromJson(response.body?.string(), CommentSent::class.java)
                callback.onSuccess(result, result.code)
            }
        })
    }
}