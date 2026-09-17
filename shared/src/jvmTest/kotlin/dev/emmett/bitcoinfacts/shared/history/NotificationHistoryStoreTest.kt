package dev.emmett.bitcoinfacts.shared.history

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NotificationHistoryStoreTest {

    private val store = NotificationHistoryStore(newTestDatabase())

    @Test
    fun `no facts are shown initially`() {
        assertTrue(store.shownFactIds().isEmpty())
    }

    @Test
    fun `marking facts shown accumulates them`() {
        store.markShown("fact-001")
        store.markShown("fact-002")
        assertEquals(setOf("fact-001", "fact-002"), store.shownFactIds())
    }

    @Test
    fun `resetCycle clears the shown set`() {
        store.markShown("fact-001")
        store.resetCycle()
        assertTrue(store.shownFactIds().isEmpty())
    }
}
