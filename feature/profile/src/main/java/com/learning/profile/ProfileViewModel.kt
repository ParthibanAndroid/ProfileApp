package com.learning.profile

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel
    @Inject
    constructor() : ViewModel() {
        private var _uiState = MutableStateFlow(ProfileUiState())
        val uiState = _uiState.asStateFlow()

        private fun validate(
            context: Context,
            state: ProfileUiState,
        ): Map<String, String> {
            val errors = mutableMapOf<String, String>()
            val nameLabel = context.getString(R.string.name_label)
            val emailLabel = context.getString(R.string.email_label)
            val phoneLabel = context.getString(R.string.phone_label)
            val photoUrlLabel = context.getString(R.string.photo_url_label)

            if (state.name.isBlank()) {
                errors[nameLabel.lowercase(Locale.getDefault())] =
                    context.getString(R.string.name_required_error)
            } else if (state.name.length < 6) {
                errors[nameLabel.lowercase(Locale.getDefault())] =
                    context.getString(R.string.name_length_error)
            }

            if (state.email.isBlank()) {
                errors[emailLabel.lowercase(Locale.getDefault())] =
                    context.getString(R.string.email_required_error)
            } else if (!Patterns.EMAIL_ADDRESS
                    .matcher(state.email)
                    .matches()
            ) {
                errors[emailLabel.lowercase(Locale.getDefault())] =
                    context.getString(R.string.invalid_email_error)
            }

            if (state.phone.isBlank()) {
                errors[phoneLabel.lowercase(Locale.getDefault())] =
                    context.getString(R.string.phone_required_error)
            } else if (state.phone.length < 10) {
                errors[phoneLabel.lowercase(Locale.getDefault())] =
                    context.getString(R.string.phone_length_error)
            }

            if (state.selectedImageUri == null) {
                errors[photoUrlLabel.lowercase(Locale.getDefault())] = context.getString(R.string.select_photo_required_error)
            }

            return errors
        }

        private fun validateProfile(context: Context): Boolean {
            val errors = validate(context = context, state = _uiState.value)

            if (errors.isNotEmpty()) {
                _uiState.update {
                    it.copy(errors = errors)
                }
                return false
            }

            return true
        }

        fun onEvent(event: ProfileEvent) {
            when (event) {
                is ProfileEvent.NameChanged -> {
                    _uiState.update { it.copy(name = event.value, errors = it.errors - "name") }
                }

                is ProfileEvent.EmailChanged -> {
                    _uiState.update { it.copy(email = event.value, errors = it.errors - "email") }
                }

                is ProfileEvent.PhoneChanged -> {
                    _uiState.update { it.copy(phone = event.value, errors = it.errors - "phone") }
                }

                is ProfileEvent.PhotoUrlChanged -> {
                    _uiState.update { it.copy(photoUrl = event.value) }
                }

                is ProfileEvent.ProfileImageClicked -> {
                }

                is ProfileEvent.ProfileImageSelected -> {
                    _uiState.update { it.copy(selectedImageUri = event.uri) }
                }

                is ProfileEvent.SaveClicked -> {
                    if (!validateProfile(context = event.context)) {
                        return
                    }
                }

                is ProfileEvent.UpdateClicked -> {
                    if (!validateProfile(context = event.context)) {
                        return
                    }
                }

                is ProfileEvent.DeleteClicked -> {
                }
            }
        }
    }
