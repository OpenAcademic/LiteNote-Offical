/*
 * Copyright (C) 2024 The LiteNote Project
 * @author OpenAcademic
 * @version 1.0
 * 
 */
package  com.example.litenote.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.litenote.MainActivity
import com.example.litenote.R
import com.example.litenote.reciever.MessageReciever

class MessageService : Service() {
    var mReceiver : MessageReciever? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mReceiver != null) {
            unregisterReceiver(mReceiver)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onCreate() {
        super.onCreate()
        var id = "cn.tw.sar.note.service.MessageService"
        var name = resources.getString(R.string.app_licenstion)
        var description = "GuardNotificationListenerService"


        var intent = Intent(this, MainActivity::class.java)
        var pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        var notification: Notification = NotificationCompat.Builder(this, id)
            .setContentTitle(resources.getString(R.string.isstarted))
            .setContentText(resources.getString(R.string.gotoapplication))
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
            .setContentIntent(pendingIntent)
            .build()

        var manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        var channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH)
        channel.enableLights(true);
        channel.setShowBadge(true);
        // 锁定屏幕不可见
        // 不弹出通知
        channel.setSound(null, null);
        channel.enableVibration(false);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            channel.lockscreenVisibility = Notification.FOREGROUND_SERVICE_DEFAULT;
        } else {
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC;
        }
        manager.createNotificationChannel(channel);

        var notification1 = notification;
        // 通知id
        var notificationId = 2
        // 显示通知
        startForeground(notificationId, notification1)

        mReceiver = MessageReciever()
        val filter = IntentFilter("android.provider.Telephony.SMS_RECEIVED")
        registerReceiver(mReceiver, filter)

    }
}