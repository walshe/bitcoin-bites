package dev.emmett.bitcoinfacts.shared.notifications

import kotlin.random.Random

/**
 * Task 5.1: pure selection algorithm - no store/platform dependency, so the
 * no-repeat and reshuffle-on-exhaustion behavior (task 5.2) is fully testable
 * in isolation. [random] is injectable so tests can assert on the *set* a
 * selection is drawn from without needing a specific outcome.
 */
object DailyFactSelector {

    data class Selection(val factId: String, val wasReshuffled: Boolean)

    /**
     * @param eligibleFactIds every `notify_eligible` fact ID (the full pool).
     * @param shownFactIds fact IDs already shown in the current cycle.
     */
    fun selectNext(
        eligibleFactIds: List<String>,
        shownFactIds: Set<String>,
        random: Random = Random.Default,
    ): Selection {
        require(eligibleFactIds.isNotEmpty()) { "No notify_eligible facts available to select from" }

        val notYetShown = eligibleFactIds.filter { it !in shownFactIds }
        return if (notYetShown.isNotEmpty()) {
            Selection(factId = notYetShown.random(random), wasReshuffled = false)
        } else {
            // Every eligible fact has been shown once - the caller is responsible
            // for clearing the shown-set (task 5.3); this pick simply draws fresh
            // from the full pool for the new cycle.
            Selection(factId = eligibleFactIds.random(random), wasReshuffled = true)
        }
    }
}
