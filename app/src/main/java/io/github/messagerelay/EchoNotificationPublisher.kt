package io.github.messagerelay

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

internal data class EchoNotificationPublishResult(
    val success: Boolean,
    val error: String = ""
)

/**
 * Echo 的本机通知出口。
 *
 * 这里的“成功”只代表 Android 已接受 Echo 自己生成的通知；
 * Windows Phone Link 是否最终同步成功必须由真机验证，不能在应用内推断。
 */
internal object EchoNotificationPublisher {
    private const val CHANNEL_ID = "echo_pc_relay"
    private const val CHANNEL_NAME = "Echo 电脑中继"

    fun publish(context: Context, message: RelayMessage): EchoNotificationPublishResult {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return EchoNotificationPublishResult(false, "Echo 通知权限未授予")
        }

        return runCatching {
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = "将 Echo 筛选后的通知交给系统，供 Windows Phone Link 等系统能力同步"
                    enableVibration(false)
                    setSound(null, null)
                }
            )

            val openEcho = PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val displayTitle = when {
                message.title.isBlank() -> message.app
                message.title == message.app -> message.app
                message.title.startsWith(message.app) -> message.title
                else -> "${message.app} · ${message.title}"
            }
            val displayBody = message.body.ifBlank { "收到一条新通知" }
            val notificationId = ("${message.packageName}|${message.title}|${message.time}".hashCode() and Int.MAX_VALUE)

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setContentTitle(displayTitle)
                .setContentText(displayBody)
                .setStyle(NotificationCompat.BigTextStyle().bigText(displayBody))
                .setSubText("Echo")
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(openEcho)
                .setAutoCancel(true)
                .setSilent(true)
                .build()

            manager.notify(notificationId, notification)
            EchoNotificationPublishResult(true)
        }.getOrElse {
            EchoNotificationPublishResult(false, "Echo 本机通知创建失败：${it.javaClass.simpleName}")
        }
    }
}
