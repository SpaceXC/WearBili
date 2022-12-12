package cn.spacexc.wearbili.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import cn.spacexc.wearbili.Application.Companion.TAG
import cn.spacexc.wearbili.utils.CoverSharedPreferencesUtils
import cn.spacexc.wearbili.utils.LogUtils.log
import cn.spacexc.wearbili.utils.NetworkUtils
import java.io.File
import java.io.FileOutputStream

/* 
WearBili Copyright (C) 2022 XC
This program comes with ABSOLUTELY NO WARRANTY.
This is free software, and you are welcome to redistribute it under certain conditions.
*/

/*
 * Created by XC on 2022/12/10.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class ImageDownloadWorker(
    private val context: Context,
    private val workerParams: WorkerParameters
) : Worker(
    context,
    workerParams
) {
    override fun doWork(): Result {
        return try {
            Log.d(TAG, "doWork: 我开始下载封面文件咯")
            val response = NetworkUtils.getUrlWithoutCallback(
                workerParams.inputData.getString("coverUrl") ?: ""
            )
            Log.d(TAG, "doWork: 我请求到网络文件咯")
            //File(context.filesDir, "downloadedContent/${workerParams.inputData.getString("cid")}")
            val dir = File(context.filesDir, "downloadedCoverPictures/")
            dir.mkdir()
            val file = File(dir, "${workerParams.inputData.getString("cid")}.jpg")
            file.path.log("filePath")
            val outputStream = FileOutputStream(file, false)
            val picIs: ByteArray? = response.body?.bytes()!!
            outputStream.write(picIs)
            outputStream.flush()
            Log.d(TAG, "doWork: 我把封面内容写入文件咯${file.path}")
            CoverSharedPreferencesUtils.saveUrl(
                workerParams.inputData.getString("cid") ?: "",
                file.path
            )
            Result.success(workDataOf("picPath" to file.path))
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}