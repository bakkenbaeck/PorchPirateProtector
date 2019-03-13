package no.bakkenbaeck.porchpirateprotector

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.html.respondHtml
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.*
import kotlinx.html.body
import kotlinx.html.head
import kotlinx.html.p
import kotlinx.html.title
import kotlinx.serialization.json.*
import kotlinx.serialization.parse
import no.bakkenbaeck.porchpirateprotector.model.User
import no.bakkenbaeck.pppshared.model.*
import org.apache.http.HttpResponse
import org.apache.http.HttpStatus
import java.time.*
import java.time.format.*
import java.util.*
import java.util.concurrent.*

internal fun Routing.helloWorld() {
    route("/") {
        get {
            serveHTML("Home","Hello from Porch Pirate Protector's server app!")
        }
    }
}

internal fun Routing.login(database: ServerDB) {
    route("api/login") {
        post {
            val bodyText = call.receiveText()
            val credentials = UserCredentials.fromString(bodyText)
            credentials?.let {
                database.fetchUser(credentials.username)?.let {
                    if (PasswordHasher.hashedValueMatches(credentials.password, it.saltedHashedPassword)) {
                        database.updateToken(it)
                        val userToken = it.userToken()!! // This should be non null after this update.
                        call.respondText(
                            userToken.toJSONString(),
                            ContentType.Application.Json,
                            HttpStatusCode.OK
                        )
                    } else {
                        call.respond(HttpStatusCode.Unauthorized)
                    }
                } ?: call.respond(HttpStatusCode.Unauthorized)
            } ?: call.respond(HttpStatusCode.BadRequest, "Could not read credentials from your account")
        }
        get {
            serveHTML("Login","You're trying to access the login API with a GET. Good luck with that.")
        }
    }
}

internal fun Routing.createAccount(database: ServerDB) {
    route("api/createAccount") {
        post {
            val bodyText = call.receiveText()
            val credentials = UserCredentials.fromString(bodyText)
            credentials?.let {
                val user = database.fetchUser(credentials.username)
                if (user != null) {
                    call.respond(HttpStatusCode.Conflict, "User already exists for ${credentials.username}")
                } else {
                    val newUser = database.createUser(credentials)
                    val token = newUser.userToken()!! // This should die if it's null as we *just* created the token
                    call.respondText(
                        token.toJSONString(),
                        ContentType.Application.Json,
                        HttpStatusCode.Created
                    )
                }
            } ?: call.respond(HttpStatusCode.BadRequest)
        }
        get {
            serveHTML("Create Account","You're trying to access the create account API with a GET. Good luck with that.")
        }
    }
}

internal fun Routing.addDevice(database: ServerDB) {
    route("api/device/add") {
        post {
            val user = call.principal<User>()
            user?.let {
                val bodyText = call.receiveText()
                val createRequest = DeviceCreateRequest.fromJSONString(bodyText)
                createRequest?.let {
                    val device = database.fetchOrCreateDevice(it.ipAddress)
                    val returnRequest = database.addUser(user, device)
                    call.respondText(
                        returnRequest.toJSONString(),
                        ContentType.Application.Json,
                        HttpStatusCode.Created
                    )
                } ?: call.respond(HttpStatusCode.BadRequest, "Could not parse create request")
            } ?: call.respond(HttpStatusCode.Unauthorized)
        }
    }
}

internal fun Routing.deviceStatus(database: ServerDB) {
    route("api/device/status") {
        post {
            val user = call.principal<User>()
            user?.let {
                val bodyText = call.receiveText()
                val deviceRequest = DeviceRequest.fromJSONString(bodyText)
                deviceRequest?.let {
                    database.validateDeviceRequest(deviceRequest, user.id.value)?.let {
                        val lockState = it.toLockState()
                        call.respondText(
                            lockState.toJSONString(),
                            ContentType.Application.Json,
                            HttpStatusCode.OK
                        )
                    } ?: call.respond(HttpStatusCode.Forbidden, "You do not have permission to access this device")
                } ?: call.respond(HttpStatusCode.BadRequest, "Could not parse status request")
            } ?: call.respond(HttpStatusCode.Unauthorized)
        }
    }
}

internal fun Routing.updateDeviceLockState(database: ServerDB) {
    route("api/device/lock") {
        post {
            val user = call.principal<User>()
            user?.let {
                val bodyText = call.receiveText()
                val deviceRequest = DeviceRequest.fromJSONString(bodyText)
                deviceRequest?.let {
                    it.lockState?.let { lockState ->
                        database.validateDeviceRequest(deviceRequest, user.id.value)?.let { validDevice ->
                            val updatedLockState = database.updateDevice(validDevice, lockState)
                            call.respondText(
                                updatedLockState.toJSONString(),
                                ContentType.Application.Json,
                                HttpStatusCode.OK
                            )
                        } ?: call.respond(HttpStatusCode.Forbidden, "You do not have permission to access this device")
                    } ?: call.respond(HttpStatusCode.BadRequest, "Please include your desired lock state")
                } ?: call.respond(HttpStatusCode.BadRequest, "Could not parse lock state request")
            } ?: call.respond(HttpStatusCode.Unauthorized)
        }
    }
}

internal suspend fun PipelineContext<Unit, ApplicationCall>.serveHTML(
    title: String,
    message: String) {
    call.respondHtml {
        head {
            title { +"API: $title" }
        }
        body {
            p {
                + message
            }
        }
    }
}

internal fun Route.authenticate(database: ServerDB) {
    val bearer = "Bearer "
    intercept(ApplicationCallPipeline.Features) {
        val authorization = call.request.header(HttpHeaders.Authorization) ?: return@intercept
        if (!authorization.startsWith(bearer)) return@intercept
        val token = authorization.removePrefix(bearer).trim()
        val user = database.fetchUserByToken(token) ?: return@intercept
        call.authentication.principal(user)
    }
}