package dev.emmett.bitcoinfacts.shared

import dev.emmett.bitcoinfacts.taxonomy.Category

/**
 * Placeholder proving the multiplatform build chain works end to end:
 * androidApp -> shared (commonMain) -> taxonomy (commonMain).
 * Replaced by real fact-loading logic in task group 3.
 */
fun helloFromSharedModule(): String =
    "Shared module is alive. Canonical taxonomy has ${Category.entries.size} categories."
