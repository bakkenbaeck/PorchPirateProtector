package no.bakkenbaeck.pppshared.extension

public fun String.isW3CValidEmail(): Boolean {
    // https://www.w3.org/TR/2012/WD-html-markup-20120320/input.email.html#input.email.attrs.value.single
    val w3cEmailRegex = Regex("^[a-zA-Z0-9.!#\$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*\$")
    return this.matches(w3cEmailRegex)
}