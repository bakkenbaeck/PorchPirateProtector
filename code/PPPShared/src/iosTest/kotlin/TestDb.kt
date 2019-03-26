package no.bakkenbaeck.pppshared

import no.bakkenbaeck.pppshared.db.MobileDb
import no.bakkenbaeck.pppshared.db.DatabaseSchema
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.ios.NativeSqliteDriver

actual object TestDb {

    actual fun setupIfNeeded() {
        if (!MobileDb.ready) {
            val driver: SqlDriver = NativeSqliteDriver(DatabaseSchema, "test.db")
            MobileDb.setupDatabase(driver)
        }
    }

    actual fun clearDatabase() {
        MobileDb.clearDatabase()
    }
}