package com.learning.profile

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learning.network.NetworkResult
import com.learning.network.model.CreateProfileRequest
import com.learning.network.model.UpdateProfileRequest
import com.learning.profile.domain.repository.ProfileRepository
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
        private val imageFileProvider: ProfileImageFileProvider,
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
                    _uiState.update { it.copy(selectedImageUri = event.uri) }
                }

                is ProfileEvent.LoadProfile -> {
                    loadProfile()
                }

                is ProfileEvent.SaveClicked -> {
                    saveProfile()
                }

                is ProfileEvent.UpdateClicked -> {
                    updateProfile()
                }

                is ProfileEvent.DeleteClicked -> {
                    deleteProfile()
                }
            }
        }

    fun loadProfile() {
        viewModelScope.launch {
//            val profileId = _uiState.value.id ?: return@launch
            val profileId = "eccf2cf7-f22e-4d06-82b6-d92aae2cb10c"

            _uiState.update {
                it.copy(isLoading = true)
            }

            launch {
                repository
                    .observeProfile(profileId)
                    .collect { profile ->
                        if (profile != null) {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    id = profile.id,
                                    name = profile.name,
                                    email = profile.email,
                                    phone = profile.phone,
                                    photoUrl = profile.photoUrl,
                                    selectedImageUri = null,
                                )
                            }
                        }
                    }
            }

            when (val result = repository.refreshProfile(profileId)) {
                is NetworkResult.Success -> {
                    // Room will emit the updated profile.
                }

                is NetworkResult.HttpError -> {
                    _uiState.update {
                        it.copy(isLoading = false)
                    }

                    _effect.emit(
                        ProfileEffect.ShowSnackbar(
                            ProfileSnackbarError.Server(
                                code = result.code,
                                message = result.message,
                            ),
                        ),
                    )
                }

                is NetworkResult.NetworkError -> {
                    _uiState.update {
                        it.copy(isLoading = false)
                    }

                    _effect.emit(
                        ProfileEffect.ShowSnackbar(
                            ProfileSnackbarError.Network,
                        ),
                    )
                }

                is NetworkResult.UnknownError -> {
                    _uiState.update {
                        it.copy(isLoading = false)
                    }

                    _effect.emit(
                        ProfileEffect.ShowSnackbar(
                            ProfileSnackbarError.Unknown,
                        ),
                    )
                }
            }
        }
    }

        fun saveProfile() {
            viewModelScope.launch {
                if (!validateProfile()) {
                    return@launch
                }

                _uiState.update { it.copy(isLoading = true) }

                val state = _uiState.value

                val request =
                    CreateProfileRequest(
                        name = state.name,
                        email = state.email,
                        phone = state.phone,
                        photoUrl = state.photoUrl,
                    )

                when (val result = repository.createProfile(request)) {
                    is NetworkResult.Success -> {
                        val profile = result.data

                        _uiState.update {
                            it.copy(
                                id = profile.id,
                                name = profile.name,
                                email = profile.email,
                                phone = profile.phone,
                                photoUrl = profile.photoUrl,
                            )
                        }

                        uploadSelectedImageIfNeeded(profileId = profile.id, isFrom = "Create")
                    }

                    is NetworkResult.HttpError -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _effect.emit(
                            ProfileEffect.ShowSnackbar(
                                error =
                                    ProfileSnackbarError.Server(
                                        code = result.code,
                                        message = result.message,
                                    ),
                            ),
                        )
                    }

                    is NetworkResult.NetworkError -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _effect.emit(ProfileEffect.ShowSnackbar(error = ProfileSnackbarError.Network))
                    }

                    is NetworkResult.UnknownError -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _effect.emit(ProfileEffect.ShowSnackbar(error = ProfileSnackbarError.Unknown))
                    }
                }
            }
        }

        fun updateProfile() {
            viewModelScope.launch {
                if (!validateProfile()) {
                    return@launch
                }

                val state = _uiState.value

                val profileId = state.id ?: return@launch

                _uiState.update { it.copy(isLoading = true) }

                val request =
                    UpdateProfileRequest(
                        name = state.name,
                        email = state.email,
                        phone = state.phone,
                        photoUrl = state.photoUrl,
                    )

                when (val result = repository.updateProfile(id = profileId, request = request)) {
                    is NetworkResult.Success -> {
                        val profile = result.data

                        _uiState.update {
                            it.copy(
                                id = profile.id,
                                name = profile.name,
                                email = profile.email,
                                phone = profile.phone,
                                photoUrl = profile.photoUrl,
                            )
                        }

                        uploadSelectedImageIfNeeded(profileId = profileId, isFrom = "Update")
                    }

                    is NetworkResult.HttpError -> {
                        _uiState.update {
                            it.copy(isLoading = false)
                        }

                        _effect.emit(
                            ProfileEffect.ShowSnackbar(
                                ProfileSnackbarError.Server(
                                    code = result.code,
                                    message = result.message,
                                ),
                            ),
                        )
                    }

                    is NetworkResult.NetworkError -> {
                        _uiState.update {
                            it.copy(isLoading = false)
                        }

                        _effect.emit(
                            ProfileEffect.ShowSnackbar(
                                ProfileSnackbarError.Network,
                            ),
                        )
                    }

                    is NetworkResult.UnknownError -> {
                        _uiState.update {
                            it.copy(isLoading = false)
                        }

                        _effect.emit(
                            ProfileEffect.ShowSnackbar(
                                ProfileSnackbarError.Unknown,
                            ),
                        )
                    }
                }
            }
        }

        fun deleteProfile() {
            viewModelScope.launch {
                val profileId = _uiState.value.id ?: return@launch

                _uiState.update { it.copy(isLoading = true) }

                when (val result = repository.deleteProfile(id = profileId)) {
                    is NetworkResult.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                id = null,
                                name = "",
                                email = "",
                                phone = "",
                                photoUrl = "",
                                selectedImageUri = null,
                            )
                        }

                        _effect.emit(
                            ProfileEffect.ShowSnackbar(
                                ProfileSnackbarError.ProfileDeleted,
                            ),
                        )
                    }

                    is NetworkResult.HttpError -> {
                        _uiState.update {
                            it.copy(isLoading = false)
                        }

                        _effect.emit(
                            ProfileEffect.ShowSnackbar(
                                ProfileSnackbarError.Server(
                                    code = result.code,
                                    message = result.message,
                                ),
                            ),
                        )
                    }

                    is NetworkResult.NetworkError -> {
                        _uiState.update { it.copy(isLoading = false) }

                        _effect.emit(ProfileEffect.ShowSnackbar(error = ProfileSnackbarError.Network))
                    }

                    is NetworkResult.UnknownError -> {
                        _uiState.update { it.copy(isLoading = false) }

                        _effect.emit(ProfileEffect.ShowSnackbar(error = ProfileSnackbarError.Unknown))
                    }
                }
            }
        }

        fun uploadSelectedImageIfNeeded(
            profileId: String,
            isFrom: String,
        ) {
            viewModelScope.launch {
                val imageUri = _uiState.value.selectedImageUri

                if (imageUri == null) {
                    _uiState.update { it.copy(isLoading = false, selectedImageUri = null) }

                    return@launch
                }
                val file = imageFileProvider.createFileFromUri(imageUri)

                when (
                    val uploadResult =
                        repository.uploadProfileImage(id = profileId, file = file)
                ) {
                    is NetworkResult.Success -> {
                        val updatedProfile = uploadResult.data

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                name = updatedProfile.name,
                                email = updatedProfile.email,
                                phone = updatedProfile.phone,
                                photoUrl = updatedProfile.photoUrl,
                                selectedImageUri = null,
                            )
                        }

                        if (isFrom == "Create") {
                            _effect.emit(
                                ProfileEffect.ShowSnackbar(
                                    ProfileSnackbarError.ProfileCreated,
                                ),
                            )
                        } else {
                            _effect.emit(
                                ProfileEffect.ShowSnackbar(
                                    ProfileSnackbarError.ProfileUpdated,
                                ),
                            )
                        }
                    }

                    is NetworkResult.HttpError -> {
                        _uiState.update { it.copy(isLoading = false) }

                        _effect.emit(
                            ProfileEffect.ShowSnackbar(
                                error =
                                    ProfileSnackbarError.Server(
                                        code = uploadResult.code,
                                        message = uploadResult.message,
                                    ),
                            ),
                        )
                    }

                    is NetworkResult.NetworkError -> {
                        _uiState.update { it.copy(isLoading = false) }

                        _effect.emit(ProfileEffect.ShowSnackbar(error = ProfileSnackbarError.Network))
                    }

                    is NetworkResult.UnknownError -> {
                        _uiState.update { it.copy(isLoading = false) }

                        _effect.emit(ProfileEffect.ShowSnackbar(error = ProfileSnackbarError.Unknown))
                    }
                }
            }
        }
    }
