package dev.emmett.bitcoinfacts.android.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

private const val DAILY_FACT_WORK_NAME = "daily_fact_notification"

/** Task 8.1: schedules [DailyFactNotificationWorker] to run about once a day. */
fun scheduleDailyFactNotification(context: Context) {
    val request = PeriodicWorkRequestBuilder<DailyFactNotificationWorker>(1, TimeUnit.DAYS).build()
    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        DAILY_FACT_WORK_NAME,
        // KEEP: re-running this on every app launch must not reset the schedule
        // (that would mean it never actually fires if the user opens the app daily).
        ExistingPeriodicWorkPolicy.KEEP,
        request,
    )
}
