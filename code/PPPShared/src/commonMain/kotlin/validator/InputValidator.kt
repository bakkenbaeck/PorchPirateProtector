package no.bakkenbaeck.pppshared.validator

import no.bakkenbaeck.pppshared.extension.isW3CValidEmail

sealed class ValidationResult {
    class Valid: ValidationResult()
    sealed class Invalid(val reason: String): ValidationResult() {
        class WasNull(val fieldName: String): Invalid("${fieldName.capitalize()} was null")
        class WasEmpty(val fieldName: String): Invalid("${fieldName.capitalize()} was empty")
        class TooShort(val fieldName: String, val minimumLength: Int): Invalid("${fieldName.capitalize()} must be at least $minimumLength characters long.")
        class InvalidEmail(val email: String): Invalid("\"$email\" is not a valid email address.")
        class InputMismatch(val firstFieldName: String, secondFieldName: String): Invalid("${secondFieldName.capitalize()} does not match $firstFieldName.")
    }
}

object InputValidator {

    fun validateNotNull(input: String?, fieldName: String): ValidationResult {
        return if (input != null) {
            ValidationResult.Valid()
        } else {
            ValidationResult.Invalid.WasNull(fieldName)
        }
    }

    fun validateNotEmpty(input: String?, fieldName: String): ValidationResult {
        if (input != null) {
            return if (input.isNotEmpty()) {
                ValidationResult.Valid()
            } else {
                return ValidationResult.Invalid.WasEmpty(fieldName)
            }
        } else {
            return ValidationResult.Invalid.WasNull(fieldName)
        }
    }

    fun validateIsEmail(input: String?, fieldName: String): ValidationResult {
        return if (input != null) {
            if (input.isW3CValidEmail()) {
                ValidationResult.Valid()
            } else {
                ValidationResult.Invalid.InvalidEmail(input)
            }
        } else {
            ValidationResult.Invalid.WasNull(fieldName)
        }
    }

    fun validateInputAtLeastLength(
        minimumLength: Int,
        input: String?,
        fieldName: String
    ): ValidationResult {
        return if (input != null) {
            return if (input.length >= minimumLength) {
                ValidationResult.Valid()
            } else {
                ValidationResult.Invalid.TooShort(
                    fieldName = fieldName,
                    minimumLength = minimumLength
                )
            }
        } else {
            ValidationResult.Invalid.WasNull(fieldName)
        }
    }

    fun validateNonNullMatches(
        firstFieldName: String,
        firstFieldInput: String?,
        secondFieldName: String,
        secondFieldInput: String?
    ): ValidationResult {
        return if (firstFieldInput == null) {
            ValidationResult.Invalid.WasNull(fieldName = firstFieldName)
        } else {
            if (secondFieldInput == null) {
                 ValidationResult.Invalid.WasNull(fieldName = secondFieldName)
            } else {
                if (firstFieldInput == secondFieldInput) {
                    ValidationResult.Valid()
                } else {
                    ValidationResult.Invalid.InputMismatch(
                        firstFieldName = firstFieldName,
                        secondFieldName = secondFieldName
                    )
                }
            }
        }
    }
}