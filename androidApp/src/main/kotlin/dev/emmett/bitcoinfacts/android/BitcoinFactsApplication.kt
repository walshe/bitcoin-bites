package dev.emmett.bitcoinfacts.android

import android.app.Application
import dev.emmett.bitcoinfacts.android.data.AppContainer

class BitcoinFactsApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
