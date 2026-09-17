package dev.emmett.bitcoinfacts.shared.notifications

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DailyFactSelectorTest {

    @Test
    fun `throws when there are no eligible facts`() {
        assertFailsWith<IllegalArgumentException> {
            DailyFactSelector.selectNext(eligibleFactIds = emptyList(), shownFactIds = emptySet())
        }
    }

    @Test
    fun `never repeats a fact while unshown ones remain, across many random seeds`() {
        val eligible = listOf("a", "b", "c", "d")
        val shown = setOf("a", "b")
        repeat(50) { seed ->
            val selection = DailyFactSelector.selectNext(eligible, shown, random = Random(seed))
            assertFalse(selection.wasReshuffled)
            assertTrue(selection.factId !in shown)
            assertContains(eligible, selection.factId)
        }
    }

    @Test
    fun `reshuffles and draws from the full pool once every eligible fact is shown`() {
        val eligible = listOf("a", "b", "c")
        val shown = setOf("a", "b", "c")
        repeat(50) { seed ->
            val selection = DailyFactSelector.selectNext(eligible, shown, random = Random(seed))
            assertTrue(selection.wasReshuffled)
            assertContains(eligible, selection.factId)
        }
    }

    @Test
    fun `a single not-yet-shown fact is always picked deterministically`() {
        val selection = DailyFactSelector.selectNext(
            eligibleFactIds = listOf("a", "b"),
            shownFactIds = setOf("a"),
        )
        assertFalse(selection.wasReshuffled)
        assertTrue(selection.factId == "b")
    }
}
