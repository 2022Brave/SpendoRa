package com.spendora

import android.app.Application
import com.spendora.data.db.SpendoraDatabase
import com.spendora.data.repository.SpendoraRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SpendoraApplication : Application() {

    lateinit var database: SpendoraDatabase
        private set

    lateinit var repository: SpendoraRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = SpendoraDatabase.getInstance(this)
        repository = SpendoraRepository(database)

        CoroutineScope(Dispatchers.IO).launch {
            repository.initDatabase()
        }
    }

    companion object {
        lateinit var instance: SpendoraApplication
            private set
    }
}
