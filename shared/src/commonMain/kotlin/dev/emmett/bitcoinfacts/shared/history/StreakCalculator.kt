package dev.emmett.bitcoinfacts.shared.history

/**
 * Task 4.5: pure, testable streak calculation - kept separate from any store so
 * it has no SQLDelight/platform dependency. The streak is the length of the
 * consecutive run of days ending at the most recently recorded open-day, but
 * only if that most recent day is today or yesterday; any older gap means at
 * least one full day passed with no activity, so the streak is broken (0).
 */
object StreakCalculator {

    fun currentStreak(openEpochDays: Collection<Long>, todayEpochDay: Long): Int {
        if (openEpochDays.isEmpty()) return 0
        val days = openEpochDays.toSet()
        val mostRecent = days.max()
        if (todayEpochDay - mostRecent > 1) return 0

        var streak = 0
        var day = mostRecent
        while (day in days) {
            streak++
            day--
        }
        return streak
    }
}
