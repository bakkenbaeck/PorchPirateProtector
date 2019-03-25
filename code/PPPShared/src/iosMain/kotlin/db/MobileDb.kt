package no.bakkenbaeck.pppshared.db

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.ios.NativeSqliteDriver
import kotlin.native.concurrent.AtomicReference
import kotlin.native.concurrent.freeze

actual object MobileDb {
    private val driverRef = AtomicReference<SqlDriver?>(null)
    private val dbRef = AtomicReference<PPPDb?>(null)

    actual val ready: Boolean
        get() = driverRef.value != null


    actual fun setupDatabase(driver: SqlDriver) {
        val db = createQueryWrapper(driver)
        driverRef.value = driver.freeze()
        dbRef.value = db.freeze()
    }

    actual fun clearDatabase() {
        driverRef.value?.close()
        dbRef.value = null
        driverRef.value = null
    }

    //Called from Swift
    @Suppress("unused")
    fun setupDefaultDriver() {
        MobileDb.setupDatabase(NativeSqliteDriver(DatabaseSchema, "PPPDb"))
    }

    actual val instance: PPPDb
        get() = dbRef.value!!
}