package io.github.messagerelay

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat

/**
 * 仅负责 Android 13+ 的 POST_NOTIFICATIONS 一次性系统权限门。
 * 授权或拒绝后立即进入现有 MainActivity，不承载业务状态。
 */
class EchoLauncherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (needsNotificationPermission() && !hasAskedNotificationPermission()) {
            markNotificationPermissionAsked()
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_POST_NOTIFICATIONS)
        } else {
            openEcho()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_POST_NOTIFICATIONS) {
            openEcho()
        }
    }

    private fun needsNotificationPermission(): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED

    private fun hasAskedNotificationPermission(): Boolean =
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getBoolean(KEY_ASKED_POST_NOTIFICATIONS, false)

    private fun markNotificationPermissionAsked() {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_ASKED_POST_NOTIFICATIONS, true)
            .apply()
    }

    private fun openEcho() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private companion object {
        const val REQUEST_POST_NOTIFICATIONS = 2101
        const val PREFS_NAME = "echo_permission_gate"
        const val KEY_ASKED_POST_NOTIFICATIONS = "asked_post_notifications"
    }
}
