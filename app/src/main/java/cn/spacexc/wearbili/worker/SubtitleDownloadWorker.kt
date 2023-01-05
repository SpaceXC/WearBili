package cn.spacexc.wearbili.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import cn.spacexc.wearbili.utils.NetworkUtils
import cn.spacexc.wearbili.utils.SubtitleSharedPreferencesUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/* 
WearBili Copyright (C) 2023 XC
This program comes with ABSOLUTELY NO WARRANTY.
This is free software, and you are welcome to redistribute it under certain conditions.
*/

/*
 * Created by XC on 2023/1/5.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class SubtitleDownloadWorker(
    private val context: Context,
    private val workerParams: WorkerParameters
) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        val fileUrl = workerParams.inputData.getString("fileUrl")
        val fileName = workerParams.inputData.getString("fileName")
        val filePath = workerParams.inputData.getString("filePath")
        val cid = workerParams.inputData.getString("cid")
        return if (!(fileUrl.isNullOrEmpty() && fileName.isNullOrEmpty() && cid.isNullOrEmpty())) {
            try {
                val response = NetworkUtils.getUrlWithoutCallback(fileUrl!!)
                val dir = File(context.filesDir, filePath!!)
                dir.mkdir()
                val file = File(dir, fileName!!)
                val outputStream = FileOutputStream(file, false)
                val fileInputStream: ByteArray? = response.body?.bytes()
                outputStream.write(fileInputStream)
                outputStream.flush()
                SubtitleSharedPreferencesUtils.saveUrl(cid!!, file.path)
                Result.success()
            } catch (e: IOException) {
                Result.failure()
            }
        } else {
            Result.failure()
        }
    }
}