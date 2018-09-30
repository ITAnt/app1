package com.jancar.radio.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

import com.jancar.radio.R;
import com.jancar.radio.RadioWrapper;
import com.jancar.utils.Logcat;



/**
 * @className: RadioNotification
 * @describe_: 状态栏显示收音机信息
 * @author___: Jan
 * @date_____: 2018/8/6 17:23
 * @version__: v1.0
 */
public class RadioNotification {
    private static final int NOTIFY_ID = 100;
    public static final String ACTION_EXIT = "ACTION.EXIT";
    public static final String ACTION_NEXT = "ACTION.NEXT";
    public static final String ACTION_PREV = "ACTION.PREV";

    private Context mContext;
    private Notification mNotification;
    private NotificationManager mNotificationManager;
    public NotificationCallback mNotificationCallback;
    
    public RadioNotification() {
        mContext = null;
        mNotification = null;
        mNotificationCallback = null;
    }
    
    public void hide() {
        if (mNotificationManager != null) {
            mNotificationManager.cancel(NOTIFY_ID);
            mNotificationManager = null;
        }
    }
    
    public void onCreate(@NonNull final Context context, final NotificationCallback notificationCallback) {
        mContext = context;
        mNotificationCallback = notificationCallback;
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_PREV);
        intentFilter.addAction(ACTION_NEXT);
        intentFilter.addAction(ACTION_EXIT);
        mContext.registerReceiver(mBroadcastReceiver, intentFilter);
    }
    
    public void onDestroy() {
        if (mContext != null) {
            mContext.unregisterReceiver(mBroadcastReceiver);
        }
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if (TextUtils.equals(action, ACTION_PREV)) {
                if (RadioNotification.this.mNotificationCallback != null) {
                    RadioNotification.this.mNotificationCallback.onPrev();
                }
            } else if (TextUtils.equals(action, ACTION_NEXT)) {
                if (RadioNotification.this.mNotificationCallback != null) {
                    RadioNotification.this.mNotificationCallback.onNext();
                }
            } else if (TextUtils.equals(action, ACTION_EXIT) && RadioNotification.this.mNotificationCallback != null) {
                RadioNotification.this.mNotificationCallback.onExit();
            }
        }
    };
    
    public void show() {
        if (mContext != null) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
            builder.setShowWhen(false);

            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.layout_notification);
            views.setOnClickPendingIntent(R.id.iv_notification_prev, PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_PREV), 0));
            views.setOnClickPendingIntent(R.id.iv_notification_next, PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_NEXT), 0));
            views.setOnClickPendingIntent(R.id.iv_notification_exit, PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_EXIT), 0));

            PendingIntent activity = PendingIntent.getActivity(mContext, 0, mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName()), 0);
            builder.setSmallIcon(R.mipmap.ic_statusbar_radio);
            builder.setContentIntent(activity);
            builder.setContent(views);
            builder.setPriority(2);

            mNotificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotification = builder.build();
            if (mNotification != null) {
                mNotification.flags = 32;
                mNotification.contentView = views;
                mNotification.bigContentView = views;
            }
            mNotificationManager.notify(NOTIFY_ID, mNotification);
        }
    }
    
    public void update(final int ifreq, final int iband, final int ilocation,int band) {
        Logcat.d("freq = " + ifreq + ", band = " + iband + ", location = " + ilocation);

        if (mNotification != null) {
            String strband = null;
            switch (band){
                case 0:
                    strband="FM1";
                    break;
                case 1:
                    strband="FM2";
                    break;
                case 2:
                    strband="FM3";
                    break;
                case 3:
                    strband="AM1";
                    break;
                case 4:
                    strband="AM2";
                    break;
                case 5:
                    strband="AM3";
                    break;
            }
            String strunit = (iband == 1) ? "MHz" : "KHz";
            String strfreq = RadioWrapper.getFreqString(ifreq, iband, ilocation);

            mNotification.contentView.setTextViewText(R.id.tv_notification_title, strfreq + " " + strunit);
            mNotification.contentView.setViewVisibility(R.id.tv_notification_artist, View.VISIBLE);
            mNotification.contentView.setTextViewText(R.id.tv_notification_artist, strband);
            mNotification.contentView.setTextViewText(R.id.tv_app_name, mContext.getString(R.string.app_name));
            mNotificationManager.notify(NOTIFY_ID, mNotification);
        }
    }
    
    public void updateScan() {
        if (mNotification != null) {
            mNotification.contentView.setTextViewText(R.id.tv_notification_title, mContext.getString(R.string.radio_scaning));
            mNotification.contentView.setViewVisibility(R.id.tv_notification_artist, View.GONE);
            mNotificationManager.notify(NOTIFY_ID, mNotification);
        }
    }
    
    public interface NotificationCallback {
        void onExit();
        void onNext();
        void onPrev();
    }
}
