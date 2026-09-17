package dev.emmett.bitcoinfacts.shared.history

import dev.emmett.bitcoinfacts.shared.db.BitcoinFactsDatabase
import kotlinx.datetime.Clock

/** Task 4.2: favorite/unfavorite read+write operations, backed by SQLDelight. */
class FavoritesStore(private val database: BitcoinFactsDatabase) {

    fun isFavorite(factId: String): Boolean =
        database.favoritesQueries.isFavorite(factId).executeAsOne()

    fun setFavorite(factId: String, favorite: Boolean) {
        if (favorite) {
            database.favoritesQueries.addFavorite(factId, Clock.System.now().toEpochMilliseconds())
        } else {
            database.favoritesQueries.removeFavorite(factId)
        }
    }

    fun allFavoriteIds(): List<String> =
        database.favoritesQueries.allFavoriteIds().executeAsList()
}
