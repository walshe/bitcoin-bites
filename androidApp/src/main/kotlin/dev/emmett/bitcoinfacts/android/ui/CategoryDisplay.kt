package dev.emmett.bitcoinfacts.android.ui

import dev.emmett.bitcoinfacts.taxonomy.Category

/** Turns a kebab-case taxonomy id ("proof-of-work") into a display label ("Proof Of Work"). */
fun Category.displayName(): String =
    id.split('-').joinToString(" ") { word -> word.replaceFirstChar { it.uppercase() } }
