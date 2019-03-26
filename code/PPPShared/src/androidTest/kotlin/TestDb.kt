package no.bakkenbaeck.pppshared

import no.bakkenbaeck.pppshared.db.MobileDb
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import no.bakkenbaeck.pppshared.db.DatabaseSchema

actual object TestDb {

    actual fun setupIfNeeded() {
        val driver = JdbcSqliteDriver()
        DatabaseSchema.create(driver)
        MobileDb.setupDatabase(driver)
    }

    actual fun clearDatabase() {
        MobileDb.clearDatabase()
    }
}