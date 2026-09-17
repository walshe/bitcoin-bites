package dev.emmett.bitcoinfacts.shared.history

import dev.emmett.bitcoinfacts.shared.db.BitcoinFactsDatabase

/** Which fact a user last had open in a given swipeable journey mode, so reopening the app can resume there. */
enum class JourneyMode(val id: String) {
    GUIDED("guided"),
    RANDOM("random"),
}

/** Remembers the last fact viewed per [JourneyMode], backed by SQLDelight. */
class JourneyProgressStore(private val database: BitcoinFactsDatabase) {

    fun setLastFactId(mode: JourneyMode, factId: String) {
        database.journeyProgressQueries.setProgress(mode.id, factId)
    }

    fun lastFactId(mode: JourneyMode): String? =
        database.journeyProgressQueries.getProgress(mode.id).executeAsOneOrNull()
}
