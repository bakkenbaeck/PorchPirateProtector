package no.bakkenbaeck.pppshared

import no.bakkenbaeck.pppshared.db.MobileDb

expect object TestDb {

    fun setupIfNeeded()

    fun clearDatabase()
}