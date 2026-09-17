package dev.emmett.bitcoinfacts.shared.history

import dev.emmett.bitcoinfacts.shared.db.BitcoinFactsDatabase
import kotlinx.datetime.Clock

/**
 * Task 4.3: cycle-scoped no-repeat tracking for daily-fact-notifications.
 * Cleared entirely by [resetCycle] once every eligible fact has been shown once
 * (task group 5's job to detect that and call it) - see design.md decision 9's
 * correction for why this must stay separate from the durable app-open log.
 */
class NotificationHistoryStore(private val database: BitcoinFactsDatabase) {

    fun shownFactIds(): Set<String> =
        database.notificationHistoryQueries.allShownFactIds().executeAsList().toSet()

    fun markShown(factId: String) {
        database.notificationHistoryQueries.markShown(factId, Clock.System.now().toEpochMilliseconds())
    }

    fun resetCycle() {
        database.notificationHistoryQueries.clearShownHistory()
    }
}
