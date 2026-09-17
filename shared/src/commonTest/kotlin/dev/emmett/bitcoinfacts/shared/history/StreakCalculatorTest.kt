package dev.emmett.bitcoinfacts.shared.history

import kotlin.test.Test
import kotlin.test.assertEquals

class StreakCalculatorTest {

    @Test
    fun `no recorded days means zero streak`() {
        assertEquals(0, StreakCalculator.currentStreak(emptyList(), todayEpochDay = 100))
    }

    @Test
    fun `a single day open today is a streak of one`() {
        assertEquals(1, StreakCalculator.currentStreak(listOf(100L), todayEpochDay = 100))
    }

    @Test
    fun `consecutive days ending yesterday still count (today not yet opened)`() {
        assertEquals(3, StreakCalculator.currentStreak(listOf(97L, 98L, 99L), todayEpochDay = 100))
    }

    @Test
    fun `consecutive days ending today count fully`() {
        assertEquals(5, StreakCalculator.currentStreak(listOf(96L, 97L, 98L, 99L, 100L), todayEpochDay = 100))
    }

    @Test
    fun `a gap before the most recent day breaks the streak to zero`() {
        // most recent activity was 2 days ago -> streak is broken, regardless of older history
        assertEquals(0, StreakCalculator.currentStreak(listOf(90L, 91L, 92L, 98L), todayEpochDay = 100))
    }

    @Test
    fun `a gap in the middle of history does not extend past it`() {
        // days 100,99 are consecutive; day 90 is an unrelated earlier isolated day
        assertEquals(2, StreakCalculator.currentStreak(listOf(90L, 99L, 100L), todayEpochDay = 100))
    }
}
