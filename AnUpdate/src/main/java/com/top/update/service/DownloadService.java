/*
 * Copyright (C) 2018 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.top.update.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import static com.top.update.entity.UpdateError.ERROR.*;
import static com.top.update.widget.UpdateDialogFragment.*;

import com.top.update.R;
import com.top.update.XUpdate;
import com.top.update._XUpdate;
import com.top.update.entity.DownloadEntity;
import com.top.update.entity.UpdateEntity;
import com.top.update.logs.UpdateLog;
import com.top.update.proxy.IUpdateHttpService;
import com.top.update.utils.ApkInstallUtils;
import com.top.update.utils.FileUtils;
import com.top.update.utils.UpdateUtils;

import java.io.File;



/**
 * APK下载服务
 *
 * @author xuexiang
 * @since 2018/7/5 上午11:15
 */
public class DownloadService extends Service {

    private static final int DOWNLOAD_NOTIFY_ID = 1000;

    private static boolean mIsRunning = false;

    private static final String CHANNEL_ID = "app_update_channel_id";
    private static final CharSequence CHANNEL_NAME = "App更新通知";

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;

    //=====================绑定服务============================//

    /**
     * 绑定服务
     *
     * @param connection
     */
    public static void bindService(ServiceConnection connection) {
        Intent intent = new Intent(XUpdate.getContext(), DownloadService.class);
        XUpdate.getContext().startService(intent);
        XUpdate.getContext().bindService(intent, connection, Context.BIND_AUTO_CREATE);
        mIsRunning = true;
    }

    /**
     * 停止下载服务
     *
     * @param contentText
     */
    private void stop(String contentText) {
        if (mBuilder != null) {
            mBuilder.setContentTitle(UpdateUtils.getAppName(DownloadService.this))
                    .setContentText(contentText);
            Notification notification = mBuilder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            mNotificationManager.notify(DOWNLOAD_NOTIFY_ID, notification);
        }
        close();
    }

    /**
     * 关闭服务
     */
    private void close() {
        mIsRunning = false;
        stopSelf();
    }

    //=====================生命周期============================//
    /**
     * 下载服务是否在运行
     *
     * @return
     */
    public static boolean isRunning() {
        return mIsRunning;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mIsRunning = true;
        return new DownloadBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mIsRunning = false;
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        mNotificationManager = null;
        mBuilder = null;
        super.onDestroy();
    }

    //========================下载通知===================================//

    /**
     * 创建通知
     */
    private void setUpNotification(@NonNull DownloadEntity downloadEntity) {
        if (!downloadEntity.isShowNotification()) {
            return;
        }

        initNotification();
    }

    /**
     * 初始化通知
     */
    private void initNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            //设置绕过免打扰模式
//            channel.setBypassDnd(false);
//            //检测是否绕过免打扰模式
//            channel.canBypassDnd();
//            //设置在锁屏界面上显示这条通知
//            channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
//            channel.setLightColor(Color.GREEN);
//            channel.setShowBadge(true);
//            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            channel.enableVibration(false);
            channel.enableLights(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                channel.canBubble();
            }

            mNotificationManager.createNotificationChannel(channel);
        }

        mBuilder = getNotificationBuilder();

        //点击广播监听
        Intent intentClick = new Intent(this, NotificationBroadcastReceiver.class);
        intentClick.setAction("notification_clicked");
        intentClick.putExtra(NotificationBroadcastReceiver.TYPE, DOWNLOAD_NOTIFY_ID);
        intentClick.putExtra("MESSAGE","消息");
        PendingIntent pendingIntentClick = PendingIntent.getBroadcast(this, 0, intentClick, PendingIntent.FLAG_ONE_SHOT);

        //cancle广播监听
        Intent intentCancel = new Intent(this, NotificationBroadcastReceiver.class);
        intentCancel.setAction("notification_cancelled");
        intentCancel.putExtra(NotificationBroadcastReceiver.TYPE, DOWNLOAD_NOTIFY_ID);
        PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(this, 0, intentCancel, PendingIntent.FLAG_ONE_SHOT);


        mBuilder.setContentIntent(pendingIntentClick);
        mBuilder.setDeleteIntent(pendingIntentCancel);
        mNotificationManager.notify(DOWNLOAD_NOTIFY_ID, mBuilder.build());
    }

    private NotificationCompat.Builder getNotificationBuilder() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.xupdate_start_download))
                .setContentText(getString(R.string.xupdate_connecting_service))
                .setSmallIcon(R.drawable.xupdate_icon_app_update)
                .setLargeIcon(UpdateUtils.drawable2Bitmap(UpdateUtils.getAppIcon(DownloadService.this)))
                .setOngoing(true)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis());
    }

    /**
     * DownloadBinder中定义了一些实用的方法
     *
     * @author user
     */
    public class DownloadBinder extends Binder {

        private FileDownloadCallBack mFileDownloadCallBack;

        private UpdateEntity mUpdateEntity;
        /**
         * 开始下载
         *
         * @param updateEntity      新app信息
         * @param downloadListener  下载监听
         */
        public void start(@NonNull UpdateEntity updateEntity, @Nullable OnFileDownloadListener downloadListener) {
            //下载
            mUpdateEntity = updateEntity;
            startDownload(updateEntity, mFileDownloadCallBack = new FileDownloadCallBack(updateEntity, downloadListener));
        }

        /**
         * 停止下载服务
         *
         * @param msg
         */
        public void stop(String msg) {
            if (mFileDownloadCallBack != null) {
                mFileDownloadCallBack.onCancel();
            }
            mUpdateEntity.getIUpdateHttpService().cancelDownload(mUpdateEntity.getDownloadUrl());
            DownloadService.this.stop(msg);
        }

        /**
         * 显示通知
         */
        public void showNotification() {
            if (mBuilder == null && DownloadService.isRunning()) {
                initNotification();
            }
        }
    }

    /**
     * 下载模块
     */
    private void startDownload(@NonNull UpdateEntity updateEntity, @NonNull FileDownloadCallBack fileDownloadCallBack) {
        String apkUrl = updateEntity.getDownloadUrl();
        if (TextUtils.isEmpty(apkUrl)) {
            String contentText = getString(R.string.xupdate_tip_download_url_error);
            stop(contentText);
            return;
        }
        String apkName = UpdateUtils.getApkNameByDownloadUrl(apkUrl);

        File apkCacheDir = FileUtils.getFileByPath(updateEntity.getApkCacheDir());
        if (apkCacheDir == null) {
            apkCacheDir = UpdateUtils.getDefaultDiskCacheDir();
        }
        try {
            if (!FileUtils.isFileExists(apkCacheDir)) {
                apkCacheDir.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String target = apkCacheDir + File.separator + updateEntity.getVersionName();

        UpdateLog.d("开始下载更新文件, 下载地址:" + apkUrl + ", 保存路径:" + target + ", 文件名:" + apkName);
        updateEntity.getIUpdateHttpService().download(apkUrl, target, apkName, fileDownloadCallBack);
    }


    /**
     * 文件下载处理
     */
    private class FileDownloadCallBack implements IUpdateHttpService.DownloadCallback {

        private final DownloadEntity mDownloadEntity;
        /**
         * 文件下载监听
         */
        private OnFileDownloadListener mOnFileDownloadListener;

        /**
         * 是否下载完成后自动安装
         */
        private boolean mIsAutoInstall;

        private int mOldRate = 0;

        private boolean mIsCancel;

        FileDownloadCallBack(@NonNull UpdateEntity updateEntity, @Nullable OnFileDownloadListener listener) {
            mDownloadEntity = updateEntity.getDownLoadEntity();
            mIsAutoInstall = updateEntity.isAutoInstall();
            mOnFileDownloadListener = listener;
        }

        @Override
        public void onStart() {
            if (mIsCancel) {
                return;
            }

            //清空通知栏状态
            mNotificationManager.cancel(DOWNLOAD_NOTIFY_ID);
            mBuilder = null;

            //初始化通知栏
            setUpNotification(mDownloadEntity);
            if (mOnFileDownloadListener != null) {
                mOnFileDownloadListener.onStart();
            }
        }

        @Override
        public void onProgress(float progress, long total) {
            if (mIsCancel) {
                return;
            }

            //做一下判断，防止自回调过于频繁，造成更新通知栏进度过于频繁，而出现卡顿的问题。
            int rate = Math.round(progress * 100);
            if (mOldRate != rate) {
                if (mOnFileDownloadListener != null) {
                    mOnFileDownloadListener.onProgress(progress, total);
                }

                if (mBuilder != null) {
                    mBuilder.setContentTitle(getString(R.string.xupdate_lab_downloading) + UpdateUtils.getAppName(DownloadService.this))
                            .setContentText(rate + "%")
                            .setProgress(100, rate, false)
                            .setWhen(System.currentTimeMillis());
                    Notification notification = mBuilder.build();
                    notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONLY_ALERT_ONCE;
                    mNotificationManager.notify(DOWNLOAD_NOTIFY_ID, notification);
                }
                //重新赋值
                mOldRate = rate;
            }
        }

        @Override
        public void onSuccess(File file) {
            if (mIsCancel) {
                return;
            }

            if (mOnFileDownloadListener != null) {
                if (!mOnFileDownloadListener.onCompleted(file)) {
                    close();
                    return;
                }
            }
            UpdateLog.d("更新文件下载完成, 文件路径:" + file.getAbsolutePath());
            try {
                if (UpdateUtils.isAppOnForeground(DownloadService.this)) {
                    //App前台运行
                    mNotificationManager.cancel(DOWNLOAD_NOTIFY_ID);

                    if (mIsAutoInstall) {
                        _XUpdate.startInstallApk(DownloadService.this, file, mDownloadEntity);
                    } else {
                        showDownloadCompleteNotification(file);
                    }
                } else {
                    showDownloadCompleteNotification(file);
                }
                //下载完自杀
                close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                close();
            }
        }

        @Override
        public void onError(Throwable throwable) {
            if (mIsCancel) {
                return;
            }

            _XUpdate.onUpdateError(DOWNLOAD_FAILED, throwable.getMessage());
            //App前台运行
            if (mOnFileDownloadListener != null) {
                mOnFileDownloadListener.onError(throwable);
            }
            try {
                mNotificationManager.cancel(DOWNLOAD_NOTIFY_ID);
                close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 取消下载
         */
        void onCancel() {
            mOnFileDownloadListener = null;
            mIsCancel = true;
        }
    }

    private void showDownloadCompleteNotification(File file) {
        //App后台运行
        //更新参数,注意flags要使用FLAG_UPDATE_CURRENT
        Intent installAppIntent = ApkInstallUtils.getInstallAppIntent(file);
        PendingIntent contentIntent = PendingIntent.getActivity(DownloadService.this, 0, installAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (mBuilder == null) {
            mBuilder = getNotificationBuilder();
        }
        mBuilder.setContentIntent(contentIntent)
                .setContentTitle(UpdateUtils.getAppName(DownloadService.this))
                .setContentText(getString(R.string.xupdate_download_complete))
                .setProgress(0, 0, false)
                //                        .setAutoCancel(true)
                .setDefaults((Notification.DEFAULT_ALL));
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(DOWNLOAD_NOTIFY_ID, notification);
    }




    public static class NotificationBroadcastReceiver extends BroadcastReceiver{

        public static final String TYPE = "type"; //这个type是为了Notification更新信息的，这个不明白的朋友可以去搜搜，很多

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int type = intent.getIntExtra(TYPE, -1);

            if (type != -1) {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(type);
            }

            if (action.equals("notification_clicked")) {
                //处理点击事件
                String message = intent.getStringExtra("MESSAGE");
                //Toast.makeText(context, "clicked " + message, Toast.LENGTH_LONG).show();
            }

            if (action.equals("notification_cancelled")) {
                //处理滑动清除和点击删除事件
                //Toast.makeText(context, "cancelled", Toast.LENGTH_LONG).show();
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(type);
            }
        }

    }
}
