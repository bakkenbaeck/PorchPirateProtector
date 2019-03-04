package no.bakkenbaeck.pppshared.api

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.url
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import no.bakkenbaeck.pppshared.ApplicationDispatcher

sealed class RequestMethod(val stringValue: String) {
    class Get: RequestMethod("GET")
    class Put: RequestMethod("PUT")
    class Post(val body: String): RequestMethod("POST")
    class Patch(val body: String): RequestMethod("PATCH")
    class Delete: RequestMethod("DELETE")
}

sealed class NetworkResult<T> {
    class Success<T>(val item: T) : NetworkResult<T>()
    class Error<T>(val error: Throwable): NetworkResult<T>()
}

open class NetworkClient(val rootURLString: String) {

    //https://ktor.io/clients/http-client/calls/requests.html
    private val ktorClient = HttpClient()

    private fun fullURLStringForPath(path: String): String {
        return "$rootURLString/$path"
    }

    fun executeRequest(
        method: RequestMethod = RequestMethod.Get(),
        path: String,
        callback: (NetworkResult<String>) -> Unit
    ) {
        GlobalScope.launch(ApplicationDispatcher) {
            try {
                val result = execute(method, path)
                callback(NetworkResult.Success(result))
            } catch (exception: Exception) {
                callback(NetworkResult.Error(exception))
            }
        }
    }

    suspend fun execute(method: RequestMethod = RequestMethod.Get(),
                        path: String): String {
        return when (method) {
            is RequestMethod.Get -> get(path)
            is RequestMethod.Post -> post(path, method.body)
            else -> "NOT IMPLEMENTED"
        }
    }

    private suspend fun get(
        path: String): String {
        val fullPath = fullURLStringForPath(path)
        return ktorClient.get {
            url(fullPath)
        }
    }

    private suspend fun post(
        path: String,
        data: String
    ): String {
        val fullPath = fullURLStringForPath(path)
        return ktorClient.post {
            url(fullPath)
            body = data
        }
    }
}