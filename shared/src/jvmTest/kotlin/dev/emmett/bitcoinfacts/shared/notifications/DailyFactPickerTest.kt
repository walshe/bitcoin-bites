package dev.emmett.bitcoinfacts.shared.notifications

import dev.emmett.bitcoinfacts.shared.FactRepository
import dev.emmett.bitcoinfacts.shared.history.AppOpenLogStore
import dev.emmett.bitcoinfacts.shared.history.NotificationHistoryStore
import dev.emmett.bitcoinfacts.shared.history.newTestDatabase
import dev.emmett.bitcoinfacts.shared.model.Fact
import dev.emmett.bitcoinfacts.shared.model.Source
import dev.emmett.bitcoinfacts.taxonomy.Category
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private fun fact(id: String, notifyEligible: Boolean) = Fact(
    id = id,
    title = "Title $id",
    categories = listOf(Category.HISTORY),
    sources = listOf(Source("src", "https://example.com")),
    notifyEligible = notifyEligible,
    difficulty = null,
    related = emptyList(),
    body = "body",
    order = 0,
)

class DailyFactPickerTest {

    private val repository = FactRepository(
        listOf(
            fact("fact-a", notifyEligible = true),
            fact("fact-b", notifyEligible = true),
            fact("fact-c", notifyEligible = true),
            fact("fact-not-eligible", notifyEligible = false),
        )
    )

    private fun newPicker(seed: Int = 42): Triple<DailyFactPicker, NotificationHistoryStore, AppOpenLogStore> {
        val db = newTestDatabase()
        val history = NotificationHistoryStore(db)
        val openLog = AppOpenLogStore(db)
        return Triple(DailyFactPicker(repository, history, openLog, Random(seed)), history, openLog)
    }

    @Test
    fun `never picks a fact that is not notify_eligible`() {
        repeat(20) { seed ->
            val (p, _, _) = newPicker(seed)
            assertTrue(p.pickTodaysFact().id != "fact-not-eligible")
        }
    }

    @Test
    fun `picking marks the fact shown`() {
        val (picker, history, _) = newPicker()
        val picked = picker.pickTodaysFact()
        assertTrue(picked.id in history.shownFactIds())
    }

    @Test
    fun `cycles through the full pool once before repeating, then reshuffles`() {
        val (picker, history, _) = newPicker()

        val firstThreePicks = (1..3).map { picker.pickTodaysFact().id }
        // All 3 eligible facts shown exactly once, no repeats yet.
        assertEquals(setOf("fact-a", "fact-b", "fact-c"), firstThreePicks.toSet())
        assertEquals(3, history.shownFactIds().size)

        // The 4th pick exhausts the pool -> reshuffle -> history reset to just this new pick.
        val fourthPick = picker.pickTodaysFact()
        assertEquals(setOf(fourthPick.id), history.shownFactIds())
    }

    @Test
    fun `picking today's fact also counts toward the open-day streak`() {
        val (picker, _, openLog) = newPicker()
        picker.pickTodaysFact()
        assertEquals(1, openLog.currentStreak())

        // A second pick on the "same day" (test runs within the same real day) must not double count.
        picker.pickTodaysFact()
        assertEquals(1, openLog.currentStreak())
    }
}
