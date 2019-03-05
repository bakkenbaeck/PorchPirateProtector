package no.bakkenbaeck.pppshared.api

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import no.bakkenbaeck.pppshared.ApplicationDispatcher

class HttpBinClient: NetworkClient("https://httpbin.org") {

    fun runGet() {
        println("HTTPBIN: Starting GET")
        GlobalScope.launch(ApplicationDispatcher) {
            try {
                val result = execute(path = "get")
                println("HTTPBIN: Success! Got: \n${result}")
            } catch (exception: Exception) {
                println("HTTPBIN: Error! : ${exception}")
            }
        }
    }

    fun runPost(body: String) {
        println("HTTPBIN: Starting POST")
        GlobalScope.launch(ApplicationDispatcher) {
            try {
                val result = execute(RequestMethod.Post(body), "post")
                println("HTTPBIN: Success! Got: \n${result}")
                println("HTTPBIN: Success! Got: \n${result}")
            } catch (exception: Exception) {
                println("HTTPBIN: Error! : ${exception}")
            }
        }
    }
}