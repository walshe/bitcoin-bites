package dev.emmett.bitcoinfacts.android.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.emmett.bitcoinfacts.android.BitcoinFactsApplication

/**
 * Task 8.1: runs once a day, picks today's fact via the shared
 * [dev.emmett.bitcoinfacts.shared.notifications.DailyFactPicker] (which owns
 * all the no-repeat/reshuffle/history side effects - this worker only fires
 * the OS notification for whatever it picked).
 */
class DailyFactNotificationWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val container = (applicationContext as BitcoinFactsApplication).container
        val fact = container.dailyFactPicker.pickTodaysFact()
        showDailyFactNotification(applicationContext, fact)
        return Result.success()
    }
}
