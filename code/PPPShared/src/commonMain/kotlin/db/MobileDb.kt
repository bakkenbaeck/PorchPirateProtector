package no.bakkenbaeck.pppshared.db

import com.squareup.sqldelight.db.SqlDriver

expect object MobileDb {
    val ready: Boolean
    val instance: PPPDb

    fun setupDatabase(driver: SqlDriver)
    fun clearDatabase()
}