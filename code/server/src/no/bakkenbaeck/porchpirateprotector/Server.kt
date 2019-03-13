package no.bakkenbaeck.porchpirateprotector

import io.ktor.application.*
import io.ktor.auth.authentication
import io.ktor.features.*
import io.ktor.html.*
import io.ktor.http.HttpHeaders
import io.ktor.request.header
import io.ktor.routing.*
import kotlinx.html.*
import no.bakkenbaeck.porchpirateprotector.model.User

fun Application.main() {
    install(DefaultHeaders)
    install(CallLogging)

    val serverDB = ServerDB()
    install(Routing) {
        // Uncomment to figure out how things are being routed
        //trace { println(it.buildText()) }

        // Actually route things
        authenticate(serverDB)
        helloWorld()
        login(serverDB)
        createAccount(serverDB)
        addDevice(serverDB)
        deviceStatus(serverDB)
        updateDeviceLockState(serverDB)
    }
}
