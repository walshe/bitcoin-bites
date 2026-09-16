package dev.emmett.bitcoinfacts.taxonomy

/**
 * The closed, canonical set of fact categories. This is the single source of truth
 * referenced by both the content compiler (validates every fact's `categories`
 * frontmatter against [id]) and the app's shared core (the `Category` filter UI).
 * See content/facts/README.md for the human-readable version of this table.
 */
enum class Category(val id: String) {
    HISTORY("history"),
    MONEY("money"),
    FIAT("fiat"),
    MONETARY_POLICY("monetary-policy"),
    PROOF_OF_WORK("proof-of-work"),
    MINING("mining"),
    NODES_NETWORK("nodes-network"),
    KEYS("keys"),
    WALLETS("wallets"),
    CUSTODY("custody"),
    MULTISIG("multisig"),
    PRIVACY("privacy"),
    SCALING("scaling"),
    SCARCITY("scarcity");

    companion object {
        private val byId = entries.associateBy { it.id }

        /** Returns the matching [Category], or null if [id] isn't part of the canonical taxonomy. */
        fun fromId(id: String): Category? = byId[id]

        val allIds: Set<String> = byId.keys
    }
}
