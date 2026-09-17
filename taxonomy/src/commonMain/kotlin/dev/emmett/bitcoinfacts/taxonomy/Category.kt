package dev.emmett.bitcoinfacts.taxonomy

/**
 * The closed, canonical set of fact categories. This is the single source of truth
 * referenced by both the content compiler (validates every fact's `categories`
 * frontmatter against [id]) and the app's shared core (the `Category` filter UI).
 * See content/facts/README.md for the human-readable version of this table.
 *
 * Grown organically from real content (design.md decision 14) rather than
 * guessed upfront - this first batch comes entirely from chapter 1 of
 * "Inventing Bitcoin" by Yan Pritzker. Add a new entry only when a real fact
 * actually needs it.
 */
enum class Category(val id: String) {
    HISTORY("history"),
    MONEY("money"),
    FIAT("fiat"),
    MONETARY_POLICY("monetary-policy"),
    SCARCITY("scarcity"),
    DECENTRALIZATION("decentralization"),
    NETWORK("network"),
    DOUBLE_SPENDING("double-spending"),
    OPEN_SOURCE("open-source"),
    PRIVACY("privacy"),
    CUSTODY("custody"),
    CENSORSHIP_RESISTANCE("censorship-resistance"),
    CRYPTOGRAPHY("cryptography");

    companion object {
        private val byId = entries.associateBy { it.id }

        /** Returns the matching [Category], or null if [id] isn't part of the canonical taxonomy. */
        fun fromId(id: String): Category? = byId[id]

        val allIds: Set<String> = byId.keys
    }
}
