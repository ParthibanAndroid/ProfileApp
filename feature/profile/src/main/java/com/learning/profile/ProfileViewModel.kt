package com.learning.profile

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {
    private var _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.UpdateName -> {
                _uiState.update { it.copy(name = event.name) }
            }

            is ProfileEvent.UpdateEmail -> {
                _uiState.update { it.copy(email = event.email) }
            }

            is ProfileEvent.UpdatePhone -> {
                _uiState.update { it.copy(phone = event.phone) }
            }

            is ProfileEvent.UpdatePhotoUrl -> {
                _uiState.update { it.copy(photoUrl = event.photoUrl) }
            }

            is ProfileEvent.GetProfile -> {

            }

            is ProfileEvent.SaveProfile -> {

            }

            is ProfileEvent.UpdateProfile -> {

            }

            is ProfileEvent.DeleteProfile -> {

            }
        }
    }
}
