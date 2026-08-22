package com.learning.profile

sealed interface ProfileValidationError {
    data object NameRequired : ProfileValidationError

    data object NameTooShort : ProfileValidationError

    data object EmailRequired : ProfileValidationError

    data object EmailInvalid : ProfileValidationError

    data object PhoneRequired : ProfileValidationError

    data object PhoneTooShort : ProfileValidationError

    data object ImageRequired : ProfileValidationError

    data object NetworkError: ProfileValidationError
}
