package dev.emmett.bitcoinfacts.shared.notifications

import dev.emmett.bitcoinfacts.shared.FactRepository
import dev.emmett.bitcoinfacts.shared.history.AppOpenLogStore
import dev.emmett.bitcoinfacts.shared.history.NotificationHistoryStore
import dev.emmett.bitcoinfacts.shared.model.Fact
import kotlin.random.Random

/**
 * Task 5.3: the interface the platform layer (Android's WorkManager job, later
 * iOS's equivalent) calls once a day. Owns the whole side-effecting sequence -
 * selecting, resetting the cycle if needed, and recording the pick - so the
 * platform layer only needs to call [pickTodaysFact] and fire an OS
 * notification with the result; it never touches storage directly.
 */
class DailyFactPicker(
    private val factRepository: FactRepository,
    private val notificationHistory: NotificationHistoryStore,
    private val appOpenLog: AppOpenLogStore,
    private val random: Random = Random.Default,
) {
    fun pickTodaysFact(): Fact {
        val eligibleIds = factRepository.all().filter { it.notifyEligible }.map { it.id }
        val selection = DailyFactSelector.selectNext(
            eligibleFactIds = eligibleIds,
            shownFactIds = notificationHistory.shownFactIds(),
            random = random,
        )

        if (selection.wasReshuffled) {
            notificationHistory.resetCycle()
        }
        notificationHistory.markShown(selection.factId)
        // Receiving a daily fact counts toward the streak the same as opening the app.
        appOpenLog.recordOpenToday()

        return factRepository.findById(selection.factId)
            ?: error("DailyFactSelector picked unknown fact id '${selection.factId}'")
    }
}
