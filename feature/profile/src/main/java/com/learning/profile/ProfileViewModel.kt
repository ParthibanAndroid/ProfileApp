package com.learning.profile

import android.util.Patterns
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel
    @Inject
    constructor() : ViewModel() {
        private var _uiState = MutableStateFlow(ProfileUiState())
        val uiState = _uiState.asStateFlow()

        private fun validate(state: ProfileUiState): Map<ProfileField, ProfileValidationError> {
            val errors = mutableMapOf<ProfileField, ProfileValidationError>()

            if (state.name.isBlank()) {
                errors[ProfileField.NAME] = ProfileValidationError.NameRequired
            } else if (state.name.length < 6) {
                errors[ProfileField.NAME] = ProfileValidationError.NameTooShort
            }

            if (state.email.isBlank()) {
                errors[ProfileField.EMAIL] = ProfileValidationError.EmailRequired
            } else if (!Patterns.EMAIL_ADDRESS
                    .matcher(state.email)
                    .matches()
            ) {
                errors[ProfileField.EMAIL] = ProfileValidationError.EmailInvalid
            }

            if (state.phone.isBlank()) {
                errors[ProfileField.PHONE] = ProfileValidationError.PhoneRequired
            } else if (state.phone.length < 10) {
                errors[ProfileField.PHONE] = ProfileValidationError.PhoneTooShort
            }

            return errors
        }

        private fun validateProfile(): Boolean {
            val state = _uiState.value
            val errors = validate(state = state)

            val imageError =
                if (state.selectedImageUri == null) {
                    ProfileValidationError.ImageRequired
                } else {
                    null
                }

            _uiState.update {
                it.copy(
                    errors = errors,
                    snackbarError = imageError,
                )
            }

            return errors.isEmpty() && imageError == null
        }

        fun onEvent(event: ProfileEvent) {
            when (event) {
                is ProfileEvent.NameChanged -> {
                    _uiState.update {
                        it.copy(
                            name = event.value,
                            errors = it.errors - ProfileField.NAME,
                        )
                    }
                }

                is ProfileEvent.EmailChanged -> {
                    _uiState.update {
                        it.copy(
                            email = event.value,
                            errors =
                                it.errors - ProfileField.EMAIL,
                        )
                    }
                }

                is ProfileEvent.PhoneChanged -> {
                    _uiState.update {
                        it.copy(
                            phone = event.value,
                            errors =
                                it.errors - ProfileField.PHONE,
                        )
                    }
                }

                is ProfileEvent.PhotoUrlChanged -> {
                    _uiState.update { it.copy(photoUrl = event.value) }
                }

                is ProfileEvent.ProfileImageClicked -> {
                }

                is ProfileEvent.ProfileImageSelected -> {
                    _uiState.update { it.copy(selectedImageUri = event.uri, snackbarError = null) }
                }

                is ProfileEvent.SnackbarErrorShown -> {
                    _uiState.update { it.copy(snackbarError = null) }
                }

                is ProfileEvent.SaveClicked -> {
                    if (!validateProfile()) {
                        return
                    }
                }

                is ProfileEvent.UpdateClicked -> {
                    if (!validateProfile()) {
                        return
                    }
                }

                is ProfileEvent.DeleteClicked -> {
                }
            }
        }
    }
