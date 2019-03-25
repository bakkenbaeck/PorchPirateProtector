package no.bakkenbaeck.pppshared.db

import com.squareup.sqldelight.db.SqlDriver

actual object MobileDb {
    private var driverRef: SqlDriver? = null
    private var dbRef: PPPDb? = null

    actual val ready: Boolean
        get() = driverRef != null

    actual fun setupDatabase(driver: SqlDriver) {
        val db = createQueryWrapper(driver)
        driverRef = driver
        dbRef = db
    }

    actual fun clearDatabase() {
        driverRef?.close()
        dbRef = null
        driverRef = null
    }

    actual val instance: PPPDb
        get() = dbRef!!
}