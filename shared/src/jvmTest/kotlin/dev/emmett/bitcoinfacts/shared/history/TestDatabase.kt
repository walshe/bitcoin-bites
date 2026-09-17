package dev.emmett.bitcoinfacts.shared.history

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import dev.emmett.bitcoinfacts.shared.db.BitcoinFactsDatabase

/** Fresh in-memory SQLite database per call, for isolated store tests on the JVM target. */
fun newTestDatabase(): BitcoinFactsDatabase {
    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    BitcoinFactsDatabase.Schema.create(driver)
    return BitcoinFactsDatabase(driver)
}
