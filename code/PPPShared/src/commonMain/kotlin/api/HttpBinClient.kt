package no.bakkenbaeck.pppshared.api

class HttpBinClient: NetworkClient("https://httpbin.org") {

    fun runGet() {
        println("HTTPBIN: Starting GET")
        executeRequest(
            path = "get",
            callback = { result ->
                when (result) {
                    is NetworkResult.Error<String> -> println("HTTPBIN: Error! : ${result.error}")
                    is NetworkResult.Success<String> -> println("HTTPBIN: Success! Got: \n${result.item}")
                }
            }
        )
    }

    fun runPost(body: String) {
        println("HTTPBIN: Starting POST")
        executeRequest(
            method = RequestMethod.Post(body),
            path = "post",
            callback = { result ->
                when (result) {
                    is NetworkResult.Error<String> -> println("HTTPBIN: Error! : ${result.error}")
                    is NetworkResult.Success<String> -> println("HTTPBIN: Success! Got: \n${result.item}")
                }
            }
        )
    }
}