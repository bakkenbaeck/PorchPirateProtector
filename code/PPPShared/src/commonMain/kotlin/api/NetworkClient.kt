package no.bakkenbaeck.pppshared.api

import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.HeadersBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import no.bakkenbaeck.pppshared.ApplicationDispatcher

sealed class RequestMethod(val stringValue: String) {
    class Get: RequestMethod("GET")
    class Put(val body: String): RequestMethod("PUT")
    class Post(val body: String): RequestMethod("POST")
    class Patch(val body: String): RequestMethod("PATCH")
    class Delete: RequestMethod("DELETE")
}

data class Header(val key: String, val value: String) {
    companion object {
        val ContentTypeJSON = Header("Content-Type", "application/json")
        val AcceptJSON = Header("Accept", "application/json")
        fun TokenAuth(token: String): Header {
            return Header("Authorization", "Token $token")
        }
    }
}

open class NetworkClient(private val rootURLString: String) {

    //https://ktor.io/clients/http-client/calls/requests.html
    private val ktorClient = HttpClient()

    private fun fullURLStringForPath(path: String): String {
        return "$rootURLString/$path"
    }

    open suspend fun execute(
        method: RequestMethod = RequestMethod.Get(),
        path: String,
        headers: List<Header> = listOf()
    ): String {
        return when (method) {
            is RequestMethod.Get -> get(path, headers)
            is RequestMethod.Put -> put(path, method.body, headers)
            is RequestMethod.Post -> post(path, method.body, headers)
            is RequestMethod.Patch -> patch(path, method.body, headers)
            is RequestMethod.Delete -> delete(path, headers)
        }
    }

    private suspend fun get(
        path: String,
        headers: List<Header>
    ): String {
        val fullPath = fullURLStringForPath(path)
        return ktorClient.get {
            url(fullPath)
            headers.forEach { header(it.key, it.value) }
        }
    }

    private suspend fun post(
        path: String,
        data: String,
        headers: List<Header>
    ): String {
        val fullPath = fullURLStringForPath(path)
        return ktorClient.post {
            url(fullPath)
            body = data
            headers.forEach { header(it.key, it.value) }
        }
    }

    private suspend fun put(
        path: String,
        data: String,
        headers: List<Header>
    ): String {
        val fullPath = fullURLStringForPath(path)
        return ktorClient.put {
            url(fullPath)
            body = data
            headers.forEach { header(it.key, it) }
        }
    }

    private suspend fun patch(
        path: String,
        data: String,
        headers: List<Header>
    ): String {
        val fullPath = fullURLStringForPath(path)
        return ktorClient.patch {
            url(fullPath)
            body = data
            headers.forEach { header(it.key, it) }
        }
    }

    private suspend fun delete(
        path: String,
        headers: List<Header>
    ): String {
        val fullPath = fullURLStringForPath(path)
        return ktorClient.delete {
            url(fullPath)
            headers.forEach { header(it.key, it) }
        }
    }
}