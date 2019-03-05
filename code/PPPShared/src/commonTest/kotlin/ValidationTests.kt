package no.bakkenbaeck.pppshared

import kotlin.test.*
import no.bakkenbaeck.pppshared.validator.*

class ValidationTests {

    @Test
    fun nullInputFailsNonNullValidator() {
        val fieldName = "field"
        val result = InputValidator.validateNotNull(null, fieldName)
        val expectedResult = ValidationResult.Invalid.WasNull(fieldName)

        when (result) {
            is ValidationResult.Valid -> fail("This should not be valid")
            is ValidationResult.Invalid ->  assertEquals(expectedResult.reason, result.reason)
        }
    }

    @Test
    fun emptyInputPassesNonNullValidator() {
        val result = InputValidator.validateNotNull("", "field")

        when (result) {
            is ValidationResult.Invalid -> fail("This should be valid")
        }
    }

    @Test
    fun nullInputFailsNonEmptyValidator() {
        val fieldName = "field"
        val result = InputValidator.validateNotEmpty(null, fieldName)
        val expectedResult = ValidationResult.Invalid.WasNull(fieldName)


        when (result) {
            is ValidationResult.Valid -> fail("This should not be valid")
            is ValidationResult.Invalid -> assertEquals(expectedResult.reason, result.reason)
        }
    }

    @Test
    fun emptyInputFailsNonEmptyValidator() {
        val fieldName = "field"
        val result = InputValidator.validateNotEmpty("", fieldName)
        val expectedResult = ValidationResult.Invalid.WasEmpty(fieldName)

        when (result) {
            is ValidationResult.Valid -> fail("This should not be valid")
            is ValidationResult.Invalid ->  assertEquals(expectedResult.reason, result.reason)
        }
    }

    @Test
    fun nonEmptyInputPassesNonEmptyValidator() {
        val result = InputValidator.validateNotEmpty("a", "field")

        when (result) {
            is ValidationResult.Invalid ->  fail("This should be valid")
        }
    }

    @Test
    fun nullInputFailsEmailValidator() {
        val fieldName = "email"
        val result = InputValidator.validateIsEmail(null, fieldName)
        val expectedResult = ValidationResult.Invalid.WasNull(fieldName)

        when (result) {
            is ValidationResult.Valid -> fail("This should not be valid")
            is ValidationResult.Invalid -> assertEquals(expectedResult.reason, result.reason)
        }
    }
}