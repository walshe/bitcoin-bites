package dev.emmett.bitcoinfacts.shared.history

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FavoritesStoreTest {

    private val store = FavoritesStore(newTestDatabase())

    @Test
    fun `a fact is not favorited by default`() {
        assertFalse(store.isFavorite("fact-001"))
    }

    @Test
    fun `favoriting then unfavoriting a fact round-trips`() {
        store.setFavorite("fact-001", true)
        assertTrue(store.isFavorite("fact-001"))

        store.setFavorite("fact-001", false)
        assertFalse(store.isFavorite("fact-001"))
    }

    @Test
    fun `allFavoriteIds only returns favorited facts`() {
        store.setFavorite("fact-001", true)
        store.setFavorite("fact-002", true)
        store.setFavorite("fact-003", true)
        store.setFavorite("fact-002", false)

        assertEquals(setOf("fact-001", "fact-003"), store.allFavoriteIds().toSet())
    }
}
