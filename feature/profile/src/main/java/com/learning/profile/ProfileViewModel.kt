package com.learning.profile

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learning.network.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel
    @Inject
    constructor(
        private val repository: ProfileRepository,
    ) : ViewModel() {
        private var _uiState = MutableStateFlow(ProfileUiState())
        val uiState = _uiState.asStateFlow()

        private var _effect = MutableSharedFlow<ProfileEffect>()
        val effect = _effect.asSharedFlow()

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

        private suspend fun validateProfile(): Boolean {
            val state = _uiState.value
            val errors = validate(state = state)

            val updatedErrors =
                if (state.selectedImageUri == null) {
                    errors + (ProfileField.IMAGE to ProfileValidationError.ImageRequired)
                } else {
                    errors
                }

            _uiState.update {
                it.copy(
                    errors = updatedErrors,
                )
            }

            if (state.selectedImageUri == null) {
                _effect.emit(
                    ProfileEffect.ShowSnackbar(
                        ProfileSnackbarError.ImageRequired,
                    ),
                )
            }

            return updatedErrors.isEmpty()
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

                is ProfileEvent.ProfileImageSelected -> {
                    _uiState.update { it.copy(selectedImageUri = event.uri, snackbarError = null) }
                }

                is ProfileEvent.SnackbarErrorShown -> {
                    _uiState.update { it.copy(snackbarError = null) }
                }

                is ProfileEvent.LoadProfile -> {
                    loadProfile()
                }

                is ProfileEvent.SaveClicked -> {
                    viewModelScope.launch {
                        if (!validateProfile()) {
                            return@launch
                        }
                    }
                }

                is ProfileEvent.UpdateClicked -> {
                    viewModelScope.launch {
                        if (!validateProfile()) {
                            return@launch
                        }
                    }
                }

                is ProfileEvent.DeleteClicked -> {
                }
            }
        }

        fun loadProfile() {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true) }

                val result = repository.getProfile(id = "be5c2f88-f1e6-48a9-ab89-56286dae8452")

                when (result) {
                    is NetworkResult.Success -> {
                        val profile = result.data

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                name = profile.name,
                                email = profile.email,
                                phone = profile.phone,
                                photoUrl = profile.photoUrl,
                            )
                        }
                    }

                    is NetworkResult.HttpError -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                snackbarError = ProfileError.Server(code = result.code, message = result.message),
                            )
                        }
                    }

                    is NetworkResult.NetworkError -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                snackbarError = ProfileError.Network,
                            )
                        }
                    }

                    is NetworkResult.UnknownError -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                snackbarError = ProfileError.Unknown,
                            )
                        }
                    }
                }
            }
        }
    }
