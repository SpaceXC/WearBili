package cn.spacexc.wearbili.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.scheduler.Requirements
import com.google.android.exoplayer2.ui.DownloadNotificationHelper
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import java.io.File
import java.util.concurrent.Executor


/**
 * Created by XC-Qan on 2022/7/30.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

object ExoPlayerUtils {
    const val DOWNLOAD_NOTIFICATION_CHANNEL_ID = "WearBiliChannelID"

    private lateinit var downloadManager: DownloadManager
    private lateinit var databaseProvider: DatabaseProvider
    private lateinit var downloadCache: Cache
    private lateinit var dataSourceFactory: HttpDataSource.Factory
    private lateinit var downloadExecutor: Executor
    private lateinit var requirements: Requirements

    fun getDownloadManager(context: Context): DownloadManager {
        databaseProvider = StandaloneDatabaseProvider(context)

        downloadCache = SimpleCache(
            getDownloadDirectory(context)!!,
            NoOpCacheEvictor(),
            databaseProvider
        )

// Create a factory for reading the data from the network.
        dataSourceFactory = DefaultHttpDataSource.Factory()

        downloadExecutor = Executor { obj: Runnable -> obj.run() }


// Create the download manager.
        downloadManager = DownloadManager(
            context,
            databaseProvider,
            downloadCache,
            dataSourceFactory,
            downloadExecutor
        )
        requirements = Requirements(Requirements.NETWORK)
        downloadManager.requirements = requirements
        downloadManager.maxParallelDownloads = 3
        return downloadManager
    }

    @Synchronized
    private fun getDownloadDirectory(context: Context): File? {
        var downloadDirectory = context.getExternalFilesDir( /* type= */null)
        if (downloadDirectory == null) {
            downloadDirectory = context.filesDir
        }
        return downloadDirectory
    }

    @Synchronized
    fun getDownloadNotificationHelper(
        context: Context
    ): DownloadNotificationHelper {
        createNotificationChannelId(context)
        return DownloadNotificationHelper(context, DOWNLOAD_NOTIFICATION_CHANNEL_ID)
    }

    fun createNotificationChannelId(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = "WearBili缓存服务"
            val descriptionText = "WearBili视频缓存"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(DOWNLOAD_NOTIFICATION_CHANNEL_ID, name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager =
                getSystemService(context, NotificationManager::class.java) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }

    }
}