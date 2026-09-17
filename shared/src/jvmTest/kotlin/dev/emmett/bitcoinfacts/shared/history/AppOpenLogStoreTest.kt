package dev.emmett.bitcoinfacts.shared.history

import kotlin.test.Test
import kotlin.test.assertEquals

class AppOpenLogStoreTest {

    // Only wiring is tested here (real clock, real DB round-trip); the actual
    // day-math is covered exhaustively by StreakCalculatorTest with injected days.
    private val store = AppOpenLogStore(newTestDatabase())

    @Test
    fun `recording today once then checking the streak gives one`() {
        store.recordOpenToday()
        assertEquals(1, store.currentStreak())
    }

    @Test
    fun `recording today twice does not double count`() {
        store.recordOpenToday()
        store.recordOpenToday()
        assertEquals(1, store.currentStreak())
    }
}
