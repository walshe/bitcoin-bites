package dev.emmett.bitcoinfacts.android.data

import android.content.Context
import dev.emmett.bitcoinfacts.shared.FactIndexLoader
import dev.emmett.bitcoinfacts.shared.FactRepository
import dev.emmett.bitcoinfacts.shared.history.AppOpenLogStore
import dev.emmett.bitcoinfacts.shared.history.FavoritesStore
import dev.emmett.bitcoinfacts.shared.history.NotificationHistoryStore
import dev.emmett.bitcoinfacts.shared.history.createBitcoinFactsDatabase
import dev.emmett.bitcoinfacts.shared.notifications.DailyFactPicker

/**
 * Manual dependency wiring - no DI framework needed at this app's size. Built
 * once in [dev.emmett.bitcoinfacts.android.BitcoinFactsApplication.onCreate] and
 * handed to composables/screens that need it.
 */
class AppContainer(context: Context) {
    val factRepository: FactRepository by lazy {
        val rawJson = context.assets.open("facts.json").bufferedReader().use { it.readText() }
        FactRepository(FactIndexLoader.parse(rawJson))
    }

    private val database by lazy { createBitcoinFactsDatabase(context) }

    val favoritesStore: FavoritesStore by lazy { FavoritesStore(database) }
    val notificationHistoryStore: NotificationHistoryStore by lazy { NotificationHistoryStore(database) }
    val appOpenLogStore: AppOpenLogStore by lazy { AppOpenLogStore(database) }

    val dailyFactPicker: DailyFactPicker by lazy {
        DailyFactPicker(factRepository, notificationHistoryStore, appOpenLogStore)
    }
}
