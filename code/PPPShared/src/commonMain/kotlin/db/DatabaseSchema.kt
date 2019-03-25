package no.bakkenbaeck.pppshared.db

import com.squareup.sqldelight.db.SqlDriver

fun createQueryWrapper(driver: SqlDriver): no.bakkenbaeck.pppshared.db.PPPDb {
    return PPPDb(driver)
}

object DatabaseSchema: SqlDriver.Schema by PPPDb.Schema {

    override fun create(driver: SqlDriver) {
        PPPDb.Schema.create(driver)
    }
}