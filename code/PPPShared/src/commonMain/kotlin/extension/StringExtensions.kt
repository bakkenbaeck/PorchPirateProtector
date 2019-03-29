package no.bakkenbaeck.pppshared.extension

fun String.isW3CValidEmail(): Boolean {
    // https://www.w3.org/TR/2012/WD-html-markup-20120320/input.email.html#input.email.attrs.value.single
    val w3cEmailRegex = Regex("^[a-zA-Z0-9.!#\$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*\$")
    return this.matches(w3cEmailRegex)
}

fun String.camelToSnakeCase(): String {
    val decapitalized = this.decapitalize()
    var text = ""
    decapitalized.forEach {
        if (it.isUppercase()) {
            text += "_${it.toLowerCase()}"
        } else {
            text += it
        }
    }
    return text
}

fun Char.isUppercase(): Boolean {
    return this.toUpperCase() == this
}