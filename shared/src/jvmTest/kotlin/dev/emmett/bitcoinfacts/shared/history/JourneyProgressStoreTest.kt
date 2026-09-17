package dev.emmett.bitcoinfacts.shared.history

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class JourneyProgressStoreTest {

    private val store = JourneyProgressStore(newTestDatabase())

    @Test
    fun `no progress recorded yet returns null`() {
        assertNull(store.lastFactId(JourneyMode.GUIDED))
    }

    @Test
    fun `remembers the last fact set for a mode`() {
        store.setLastFactId(JourneyMode.GUIDED, "fact-001")
        assertEquals("fact-001", store.lastFactId(JourneyMode.GUIDED))

        store.setLastFactId(JourneyMode.GUIDED, "fact-002")
        assertEquals("fact-002", store.lastFactId(JourneyMode.GUIDED))
    }

    @Test
    fun `guided and random progress are tracked independently`() {
        store.setLastFactId(JourneyMode.GUIDED, "fact-001")
        store.setLastFactId(JourneyMode.RANDOM, "fact-002")

        assertEquals("fact-001", store.lastFactId(JourneyMode.GUIDED))
        assertEquals("fact-002", store.lastFactId(JourneyMode.RANDOM))
    }
}
