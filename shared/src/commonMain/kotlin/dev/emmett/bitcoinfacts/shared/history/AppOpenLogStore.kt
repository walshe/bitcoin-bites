package dev.emmett.bitcoinfacts.shared.history

import dev.emmett.bitcoinfacts.shared.db.BitcoinFactsDatabase
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Durable, never-cleared log of which calendar days the user opened the app or
 * received a daily notification fact - the sole data source for [currentStreak]
 * (task 4.5). See design.md decision 9's correction for why this is a separate
 * table from [NotificationHistoryStore]'s cycle-scoped one.
 */
class AppOpenLogStore(private val database: BitcoinFactsDatabase) {

    fun recordOpenToday() {
        database.appOpenLogQueries.recordOpenDay(todayEpochDay())
    }

    fun currentStreak(): Int =
        StreakCalculator.currentStreak(
            openEpochDays = database.appOpenLogQueries.allOpenEpochDays().executeAsList(),
            todayEpochDay = todayEpochDay(),
        )

    private fun todayEpochDay(): Long =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toEpochDays().toLong()
}
