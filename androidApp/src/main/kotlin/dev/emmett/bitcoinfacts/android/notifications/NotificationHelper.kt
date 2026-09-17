package dev.emmett.bitcoinfacts.android.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import dev.emmett.bitcoinfacts.android.MainActivity
import dev.emmett.bitcoinfacts.shared.model.Fact

const val DAILY_FACT_CHANNEL_ID = "daily_fact"
const val EXTRA_FACT_ID = "factId"
private const val DAILY_FACT_NOTIFICATION_ID = 1

/** Task 8.1 (channel setup): required on Android 8+ before any notification can be shown. */
fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
    val channel = NotificationChannel(
        DAILY_FACT_CHANNEL_ID,
        "Daily Bitcoin fact",
        NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
        description = "One Bitcoin fact a day"
    }
    context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
}

/**
 * Task 8.1 (fire) + 8.2 (tap deep-link): shows the daily fact notification.
 * Tapping it opens [MainActivity] with [EXTRA_FACT_ID] set, which the activity
 * uses to navigate straight to that fact's detail screen.
 */
fun showDailyFactNotification(context: Context, fact: Fact) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        // Notification permission is optional (task 8.3 asks for it, doesn't require it) -
        // the daily pick/mark-shown logic still runs even if we can't visually notify.
        if (!granted) return
    }

    val openIntent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        putExtra(EXTRA_FACT_ID, fact.id)
    }
    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        openIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    // Using a system icon as a placeholder - a real notification icon (small,
    // white-on-transparent) is a design asset task 6.2 deliberately deferred.
    val notification = NotificationCompat.Builder(context, DAILY_FACT_CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle("Today's Bitcoin fact")
        .setContentText(fact.title)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .build()

    NotificationManagerCompat.from(context)
        .notify(DAILY_FACT_NOTIFICATION_ID, notification)
}
