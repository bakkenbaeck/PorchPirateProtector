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

    fun validate(input: String?, fieldName: String, functions: List<(String?, String) -> ValidationResult>): ValidationResult {
        for (function in functions) {
            val currentResult = function(input, fieldName)
            when (currentResult) {
                is ValidationResult.Invalid -> return currentResult
            }
        }

        return ValidationResult.Valid()
    }

    private fun notNull(input: String?, fieldName: String): ValidationResult {
        return if (input != null) {
            ValidationResult.Valid()
        } else {
            ValidationResult.Invalid.WasNull(fieldName)
        }
    }

    private fun notEmpty(input: String?, fieldName: String): ValidationResult {
        val isEmpty = input?.isEmpty() ?: true
        return if (isEmpty) {
            ValidationResult.Invalid.WasEmpty(fieldName)
        } else {
            ValidationResult.Valid()
        }
    }

    fun validateNotNull(input: String?, fieldName: String): ValidationResult {
        return validate(input, fieldName, listOf(
            ::notNull
        ))
    }

    fun validateNotNullOrEmpty(input: String?, fieldName: String): ValidationResult {
        return validate(input, fieldName, listOf(
            ::notNull,
            ::notEmpty
        ))
    }

    fun validateIsEmail(input: String?, fieldName: String): ValidationResult {
        val notEmptyResult = validateNotNullOrEmpty(input, fieldName)
        when (notEmptyResult) {
            is ValidationResult.Invalid -> return notEmptyResult
        }

        // If we're here, the input is definitely not null
        val email = input!!

        return if (email.isW3CValidEmail()) {
            ValidationResult.Valid()
        } else {
            ValidationResult.Invalid.InvalidEmail(email)
        }
    }

    fun validateInputAtLeastLength(
        minimumLength: Int,
        input: String?,
        fieldName: String
    ): ValidationResult {
        val notEmptyResult = validateNotNullOrEmpty(input, fieldName)
        when (notEmptyResult) {
            is ValidationResult.Invalid -> return notEmptyResult
        }

        return if (input!!.length >= minimumLength) {
            ValidationResult.Valid()
        } else {
            ValidationResult.Invalid.TooShort(
                fieldName = fieldName,
                minimumLength = minimumLength
            )
        }
    }

    fun validateNonNullMatches(
        firstFieldName: String,
        firstFieldInput: String?,
        secondFieldName: String,
        secondFieldInput: String?
    ): ValidationResult {
        val firstFieldNonEmptyResult = validateNotNullOrEmpty(firstFieldInput, firstFieldName)
        when (firstFieldNonEmptyResult) {
            is ValidationResult.Invalid -> return firstFieldNonEmptyResult
        }

        val secondFieldNonEmptyResult = validateNotNullOrEmpty(secondFieldInput, secondFieldName)
        when (secondFieldNonEmptyResult) {
            is ValidationResult.Invalid -> return secondFieldNonEmptyResult
        }

        // If we've gotten here, both are definitely not null
        return if (firstFieldInput!! == secondFieldInput!!) {
            ValidationResult.Valid()
        } else {
            ValidationResult.Invalid.InputMismatch(
                firstFieldName = firstFieldName,
                secondFieldName = secondFieldName
            )
        }
    }
}