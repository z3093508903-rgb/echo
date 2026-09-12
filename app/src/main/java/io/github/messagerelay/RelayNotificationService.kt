package io.github.messagerelay

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*

class RelayNotificationService : NotificationListenerService() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onListenerConnected() {
        super.onListenerConnected()
        scope.launch {
            if (AppSettingsRepository(applicationContext).current().persistentNotification) showStatusNotification()
        }
    }

    private fun showStatusNotification() {
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(NotificationChannel("relay_status", "Echo 运行状态", NotificationManager.IMPORTANCE_LOW))
        val intent = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE)
        manager.notify(1001, NotificationCompat.Builder(this, "relay_status")
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setContentTitle("Echo 正在运行")
            .setContentText("正在监听你选择的来源应用")
            .setContentIntent(intent)
            .setLocalOnly(true)
            .setOngoing(true)
            .build())
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName == packageName) return

        scope.launch {
            val dao = RelayDatabase.get(applicationContext).relayDao()
            val rule = dao.rule(sbn.packageName) ?: return@launch
            val settings = AppSettingsRepository(applicationContext).current()

            val content = if (sbn.packageName == "com.tencent.mm") {
                WeChatNotificationParser.parse(sbn)
            } else {
                NotificationContentExtractor.extract(sbn)
            }
            val hasReadableContent = content.title.isNotBlank() || content.body.isNotBlank()
            val ongoing = sbn.notification.flags and Notification.FLAG_ONGOING_EVENT != 0
            val canPostEchoNotification =
                Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                    ContextCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED

            if (
                NotificationTakeoverPolicy.shouldCancelSource(
                    hasEnabledRule = true,
                    paused = settings.paused,
                    canPostEchoNotification = canPostEchoNotification,
                    hasReadableContent = hasReadableContent,
                    ongoing = ongoing
                )
            ) {
                runCatching { cancelNotification(sbn.key) }
            }

            if (!hasReadableContent) return@launch
            if (settings.paused) return@launch

            val app = runCatching {
                packageManager.getApplicationLabel(
                    packageManager.getApplicationInfo(sbn.packageName, 0)
                ).toString()
            }.getOrDefault(sbn.packageName)
            if (SmsDuplicateGuard.shouldSuppressNotification(sbn.packageName, content.title, content.body, sbn.postTime)) return@launch
            val relayApp = if (sbn.packageName == "com.tencent.mm" && content.title.startsWith("微信 · ")) content.title else app

            RelayEngine.processSelected(
                applicationContext,
                RelayMessage(sbn.packageName, relayApp, content.title, content.body, sbn.postTime),
                rule,
                settings
            )
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
