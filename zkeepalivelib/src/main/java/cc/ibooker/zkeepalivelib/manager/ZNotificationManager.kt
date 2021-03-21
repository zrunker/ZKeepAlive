package cc.ibooker.zkeepalivelib.manager

import android.app.Notification
import android.app.NotificationChannel
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import cc.ibooker.zkeepalivelib.R

/**
 * 通知管理类
 *
 * @author 邹峰立
 */
class ZNotificationManager private constructor() {
    companion object {
        val instance: ZNotificationManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { ZNotificationManager() }
    }
    /**
     * 获取Notification
     */
    fun getNotification(context: Context,
                        channelId: String,channelName: String, contentTitle: String, contentText: String): Notification {
        val applicationContext = context.applicationContext
        val builder: Notification.Builder = Notification.Builder(applicationContext, channelId)
            .setContentIntent(PendingIntent.getActivity(applicationContext, 0, Intent(), 0))// 设置PendingIntent
            .setSmallIcon(R.mipmap.ic_launcher)// 设置状态栏内的小图标
            .setContentTitle(contentTitle)// 标题
            .setContentText(contentText)// 设置上下文内容
            .setWhen(System.currentTimeMillis())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 修改安卓8.1以上系统报错
            val notificationChannel = NotificationChannel(channelId, channelName, android.app.NotificationManager.IMPORTANCE_MIN)
            notificationChannel.enableLights(false)// 如果使用中的设备支持通知灯，则说明此通知通道是否应显示灯
            notificationChannel.setShowBadge(true)// 是否显示角标
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_SECRET
            val manager = applicationContext.getSystemService(Service.NOTIFICATION_SERVICE) as android.app.NotificationManager
            manager.createNotificationChannel(notificationChannel)
        }
        return builder.build()
    }
}