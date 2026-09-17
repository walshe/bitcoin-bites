package dev.emmett.bitcoinfacts.shared.history

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import dev.emmett.bitcoinfacts.shared.db.BitcoinFactsDatabase

/** Task 4.4: the Android-specific SQLDelight driver, backed by a real on-device SQLite file. */
fun createBitcoinFactsDatabase(context: Context): BitcoinFactsDatabase {
    val driver = AndroidSqliteDriver(
        schema = BitcoinFactsDatabase.Schema,
        context = context,
        name = "bitcoin_facts.db",
    )
    return BitcoinFactsDatabase(driver)
}
