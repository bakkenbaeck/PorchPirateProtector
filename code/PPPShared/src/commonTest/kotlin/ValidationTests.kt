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
            is ValidationResult.Invalid -> fail("This should be valid, instead it's ${result.reason}")
        }
    }

    @Test
    fun nullInputFailsNonEmptyValidator() {
        val fieldName = "field"
        val result = InputValidator.validateNotNullOrEmpty(null, fieldName)
        val expectedResult = ValidationResult.Invalid.WasNull(fieldName)


        when (result) {
            is ValidationResult.Valid -> fail("This should not be valid")
            is ValidationResult.Invalid -> assertEquals(expectedResult.reason, result.reason)
        }
    }

    @Test
    fun emptyInputFailsNonEmptyValidator() {
        val fieldName = "field"
        val result = InputValidator.validateNotNullOrEmpty("", fieldName)
        val expectedResult = ValidationResult.Invalid.WasEmpty(fieldName)

        when (result) {
            is ValidationResult.Valid -> fail("This should not be valid")
            is ValidationResult.Invalid -> assertEquals(expectedResult.reason, result.reason)
        }
    }

    @Test
    fun nonEmptyInputPassesNonEmptyValidator() {
        val result = InputValidator.validateNotNullOrEmpty("a", "field")

        when (result) {
            is ValidationResult.Invalid -> fail("This should be valid, instead it's ${result.reason}")
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

    @Test
    fun emptyInputFailsEmailValidator() {
        val fieldName = "email"
        val result = InputValidator.validateIsEmail("", fieldName)
        val expectedResult = ValidationResult.Invalid.WasEmpty(fieldName)

        when (result) {
            is ValidationResult.Valid -> fail("This should not be valid")
            is ValidationResult.Invalid -> assertEquals(expectedResult.reason, result.reason)
        }
    }

    @Test
    fun nonEmailInputFailsEmailValidator() {
        val input = "definitely not an email"
        val result = InputValidator.validateIsEmail(input, "email")
        val expectedResult = ValidationResult.Invalid.InvalidEmail(input)

        when (result) {
            is ValidationResult.Valid -> fail("This should not be valid")
            is ValidationResult.Invalid -> assertEquals(expectedResult.reason, result.reason)
        }
    }

    @Test
    fun nullInputFailsLengthValidator() {
        val fieldName = "password"
        val result = InputValidator.validateInputAtLeastLength(1, null, fieldName)
        val expectedResult = ValidationResult.Invalid.WasNull(fieldName)

        when (result) {
            is ValidationResult.Valid -> fail("This should not be valid")
            is ValidationResult.Invalid -> assertEquals(expectedResult.reason, result.reason)
        }
    }

    @Test
    fun emptyInputFailsLengthValidatorOf0() {
        val fieldName = "password"
        val result = InputValidator.validateInputAtLeastLength(0, "", fieldName)
        val expectedResult = ValidationResult.Invalid.WasEmpty(fieldName)

        when (result) {
            is ValidationResult.Valid -> fail("This should not be valid")
            is ValidationResult.Invalid -> assertEquals(expectedResult.reason, result.reason)
        }
    }

    @Test
    fun singleLetterInputPassesLengthValidatorOf1() {
        val result = InputValidator.validateInputAtLeastLength(1, "a", "password")

        when (result) {
            is ValidationResult.Invalid -> fail("This should be valid, instead it's ${result.reason}")
        }
    }

    @Test
    fun singleLetterInputFailsLengthValidatorOf2() {
        val fieldName = "password"
        val minLength = 2
        val result = InputValidator.validateInputAtLeastLength(minLength, "a", fieldName)
        val expectedResult = ValidationResult.Invalid.TooShort(fieldName, minLength)

        when (result) {
            is ValidationResult.Valid -> fail("This should not be valid")
            is ValidationResult.Invalid -> assertEquals(expectedResult.reason, result.reason)
        }
    }

    @Test
    fun nullBothInputsFailsMatchValidator() {
        val firstFieldName = "password"
        val secondFieldName = "confirm"

        val result = InputValidator.validateNonNullMatches(firstFieldName, null, secondFieldName, null)
        val expectedResult = ValidationResult.Invalid.WasNull(secondFieldName)

        when (result) {
            is ValidationResult.Valid -> fail("This should not be valid")
            is ValidationResult.Invalid -> assertEquals(expectedResult.reason, result.reason)
        }
    }

    @Test
    fun nullFirstInputFailsMatchValidator() {
        val firstFieldName = "password"
        val secondFieldName = "confirm"

        val result = InputValidator.validateNonNullMatches(firstFieldName, null, secondFieldName, "a")
        val expectedResult = ValidationResult.Invalid.WasNull(firstFieldName)

        when (result) {
            is ValidationResult.Valid -> fail("This should not be valid")
            is ValidationResult.Invalid -> assertEquals(expectedResult.reason, result.reason)
        }
    }

    @Test
    fun nullSecondInputFailsMatchValidator() {
        val firstFieldName = "password"
        val secondFieldName = "confirm"

        val result = InputValidator.validateNonNullMatches(firstFieldName, "a", secondFieldName, null)
        val expectedResult = ValidationResult.Invalid.WasNull(secondFieldName)

        when (result) {
            is ValidationResult.Valid -> fail("This should not be valid")
            is ValidationResult.Invalid -> assertEquals(expectedResult.reason, result.reason)
        }
    }

    @Test
    fun emptyBothInputsFailsMatchValidator() {
        val firstFieldName = "password"
        val secondFieldName = "confirm"

        val result = InputValidator.validateNonNullMatches(firstFieldName, "", secondFieldName, "")
        val expectedResult = ValidationResult.Invalid.WasEmpty(secondFieldName)

        when (result) {
            is ValidationResult.Valid -> fail("This should not be valid")
            is ValidationResult.Invalid -> assertEquals(expectedResult.reason, result.reason)
        }
    }

    @Test
    fun emptyFirstInputFailsMatchValidator() {
        val firstFieldName = "password"
        val secondFieldName = "confirm"

        val result = InputValidator.validateNonNullMatches(firstFieldName, "", secondFieldName, "a")
        val expectedResult = ValidationResult.Invalid.WasEmpty(firstFieldName)

        when (result) {
            is ValidationResult.Valid -> fail("This should not be valid")
            is ValidationResult.Invalid -> assertEquals(expectedResult.reason, result.reason)
        }
    }

    @Test
    fun emptySecondInputFailsMatchValidator() {
        val firstFieldName = "password"
        val secondFieldName = "confirm"

        val result = InputValidator.validateNonNullMatches(firstFieldName, "a", secondFieldName, "")
        val expectedResult = ValidationResult.Invalid.WasEmpty(secondFieldName)

        when (result) {
            is ValidationResult.Valid -> fail("This should not be valid")
            is ValidationResult.Invalid -> assertEquals(expectedResult.reason, result.reason)
        }
    }

    @Test
    fun nonMatchingNonEmptyInputsFailsMatchValidator() {
        val firstFieldName = "password"
        val secondFieldName = "confirm"

        val result = InputValidator.validateNonNullMatches(firstFieldName, "a", secondFieldName, "b")
        val expectedResult = ValidationResult.Invalid.InputMismatch(firstFieldName, secondFieldName)

        when (result) {
            is ValidationResult.Valid -> fail("This should not be valid")
            is ValidationResult.Invalid -> assertEquals(expectedResult.reason, result.reason)
        }
    }

    @Test
    fun matchingNonEmptyInputsPassesMatchValidator() {
        val result = InputValidator.validateNonNullMatches("password", "a", "confirm", "a")

        when (result) {
            is ValidationResult.Invalid -> fail("This should be valid, instead it's ${result.reason}")
        }
    }

}