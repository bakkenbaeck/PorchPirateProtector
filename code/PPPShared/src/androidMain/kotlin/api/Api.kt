package no.bakkenbaeck.pppshared.api

actual fun localhostRootURL(): String {
    /// 10.0.2.2 is the emulator address for localhost, this
    /// works regardless of which exact IP you're using.
    return "http://10.0.2.2:8080"
}
