/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.spacexc.wearbili.utils;

import static cn.spacexc.wearbili.utils.VideoDownloadUtil.DOWNLOAD_NOTIFICATION_CHANNEL_ID;

import android.app.Notification;
import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.offline.Download;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.offline.DownloadService;
import com.google.android.exoplayer2.scheduler.PlatformScheduler;
import com.google.android.exoplayer2.scheduler.Requirements;
import com.google.android.exoplayer2.scheduler.Scheduler;
import com.google.android.exoplayer2.ui.DownloadNotificationHelper;
import com.google.android.exoplayer2.util.NotificationUtil;
import com.google.android.exoplayer2.util.Util;

import java.util.List;

import cn.spacexc.wearbili.R;

/**
 * A service for downloading media.
 * From CSDN
 */
public class VideoDownloadService extends DownloadService {

  private static final int JOB_ID = 1;
  private static final int FOREGROUND_NOTIFICATION_ID = 1;

  public VideoDownloadService() {
    super(
            FOREGROUND_NOTIFICATION_ID,
            DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
            DOWNLOAD_NOTIFICATION_CHANNEL_ID,
            R.string.exo_download_notification_channel_name,
            /* channelDescriptionResourceId= */ 0);
  }

  @Override
  protected DownloadManager getDownloadManager() {
    // This will only happen once, because getDownloadManager is guaranteed to be called only once
    // in the life cycle of the process.
    DownloadManager downloadManager = VideoDownloadUtil.getDownloadManager(/* context= */ this);
    DownloadNotificationHelper downloadNotificationHelper =
            VideoDownloadUtil.getDownloadNotificationHelper(/* context= */ this);
    downloadManager.addListener(
            new TerminalStateNotificationHelper(
                    this, downloadNotificationHelper, FOREGROUND_NOTIFICATION_ID + 1));
    return downloadManager;
  }

  @Override
  protected Scheduler getScheduler() {
    return Util.SDK_INT >= 21 ? new PlatformScheduler(this, JOB_ID) : null;
  }

  @Override
  protected Notification getForegroundNotification(
          List<Download> downloads, @Requirements.RequirementFlags int notMetRequirements) {

    return VideoDownloadUtil.getDownloadNotificationHelper(/* context= */ this)
            .buildProgressNotification(
                    /* context= */ this,
                    R.drawable.cloud_download,
                    /* contentIntent= */ null,
                    /* message= */ null,
                    downloads,
                    notMetRequirements);
  }

  /**
   * Creates and displays notifications for downloads when they complete or fail.
   *
   * <p>This helper will outlive the lifespan of a single instance of {@link VideoDownloadService}.
   * It is static to avoid leaking the first {@link VideoDownloadService} instance.
   */
  private static final class TerminalStateNotificationHelper implements DownloadManager.Listener {

    private final Context context;
    private final DownloadNotificationHelper notificationHelper;

    private int nextNotificationId;

    public TerminalStateNotificationHelper(
            Context context, DownloadNotificationHelper notificationHelper, int firstNotificationId) {
      this.context = context.getApplicationContext();
      this.notificationHelper = notificationHelper;
      nextNotificationId = firstNotificationId;
    }

    @Override
    public void onDownloadChanged(
            DownloadManager downloadManager, Download download, @Nullable Exception finalException) {
      Notification notification;
      Log.d("DemoDownloadService", "=======" + download.state);
      if (download.state == Download.STATE_COMPLETED) {
        notification =
                notificationHelper.buildDownloadCompletedNotification(
                        context,
                        R.drawable.ic_baseline_done_24,
                        /* contentIntent= */ null,
                        Util.fromUtf8Bytes(download.request.data));
      } else if (download.state == Download.STATE_FAILED) {
        notification =
                notificationHelper.buildDownloadFailedNotification(
                        context,
                        R.drawable.ic_baseline_done_24,
                        /* contentIntent= */ null,
                        Util.fromUtf8Bytes(download.request.data));
      } else {
        return;
      }
      NotificationUtil.setNotification(context, nextNotificationId++, notification);
    }
  }
}
