package com.nekosoft.brokenglass.alarm;

import static com.nekosoft.brokenglass.alarm.AlarmUtils.POS_NOTIFI;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.brokenscreen.prankapp.crack.screen.R;
import com.nekosoft.brokenglass.data.local.AppPreference;
import com.nekosoft.brokenglass.ui.SplashActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NotificationUtils {


    public static void setNotification(Context context) {
        if (AppPreference.Companion.getInstance(context) != null) {
            int pos = Objects.requireNonNull(AppPreference.Companion.getInstance(context)).getInt(POS_NOTIFI, 0);
            setupNotification(context, pos);
            if (pos < 2) {
                Objects.requireNonNull(AppPreference.Companion.getInstance(context)).setInt(POS_NOTIFI, pos + 1);
            } else {
                Objects.requireNonNull(AppPreference.Companion.getInstance(context)).setInt(POS_NOTIFI, 0);
            }
        }
    }

    private static void setupNotification(Context context, int posNoti) {
        ArrayList<NotificationModel> list = new ArrayList<>();
        list.add(new NotificationModel(R.layout.layout_remote_1, R.layout.layout_remote_1_expand));
        list.add(new NotificationModel(R.layout.layout_remote_2, R.layout.layout_remote_2_expand));
        list.add(new NotificationModel(R.layout.layout_remote_3, R.layout.layout_remote_3_expand));


        Intent notificationIntent = null;
        PendingIntent contentPendingIntent = null;

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), list.get(posNoti).getLayoutRemote());
        RemoteViews remoteViewsExpand = new RemoteViews(context.getPackageName(), list.get(posNoti).getLayoutRemoteExpand());


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channel_id = createNotificationChannel(context);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channel_id)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setNotificationSilent()
                .setCustomContentView(remoteViews)
                .setAutoCancel(true)
                .setShowWhen(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setCustomBigContentView(remoteViewsExpand).setContentIntent(contentPendingIntent)
                .setOngoing(false);

        notificationIntent = new Intent(context, SplashActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            contentPendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            contentPendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);

        mBuilder.setContentIntent(contentPendingIntent);
        if (notificationManager != null) {
            notificationManager.notify(12, mBuilder.build());
        }


    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static Bitmap getBitmap(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    private static Bitmap getBitmap(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable instanceof BitmapDrawable) {
            return BitmapFactory.decodeResource(context.getResources(), drawableId);
        } else if (drawable instanceof VectorDrawable) {
            return getBitmap((VectorDrawable) drawable);
        } else {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }


    private static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (String activeProcess : processInfo.pkgList) {
                    if (activeProcess.equals(context.getPackageName())) {
                        isInBackground = false;
                    }
                }
            }
        }

        return isInBackground;
    }

    public static String createNotificationChannel(Context context) {

        // NotificationChannels are required for Notifications on O (API 26) and above.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String channelId = "Channel_id";
            CharSequence channelName = "DCT_DBC";
            String channelDescription = "Notification";
            int channelImportance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, channelImportance);
            notificationChannel.setDescription(channelDescription);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);

            return channelId;
        } else {
            return "DBC";
        }
    }

}
