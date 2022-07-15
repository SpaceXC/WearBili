package cn.spacexc.wearbili.work

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import cn.spacexc.wearbili.dataclass.download.DownloadMission
import com.google.gson.Gson

/**
 * Created by XC-Qan on 2022/7/13.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class DownloadWorker(context: Context, workerParams: WorkerParameters) : Worker(
    context,
    workerParams
) {
    override fun doWork(): Result {
        val downloadMission: DownloadMission =
            Gson().fromJson(inputData.getString("downloadMission"), DownloadMission::class.java)

        return Result.success()
    }
}