package no.bakkenbaeck.pppshared

expect object TestDb {

    fun setupIfNeeded()

    fun clearDatabase()
}