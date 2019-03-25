package no.bakkenbaeck.porchpirateprotector.application

import android.app.Application
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import no.bakkenbaeck.porchpirateprotector.manager.SharedPreferencesManager
import no.bakkenbaeck.pppshared.db.DatabaseSchema
import no.bakkenbaeck.pppshared.db.MobileDb
import no.bakkenbaeck.pppshared.manager.DeviceManager

class PPPApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        val androidDriver: SqlDriver = AndroidSqliteDriver(DatabaseSchema, this, "PPPDb")
        MobileDb.setupDatabase(androidDriver)

        // TODO: Remove when I have this squared away
        if (DeviceManager.loadPairedDevicesFromDatabase().isNullOrEmpty()) {
            SharedPreferencesManager(this).storeIPAddresses(listOf(
                "10.0.0.3",
                "1.5.6"
            ))
        }
    }
}