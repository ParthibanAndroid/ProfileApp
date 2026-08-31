package com.learning.profilelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learning.profile.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ProfileListViewModel
    @Inject
    constructor(
        repository: ProfileRepository,
    ) : ViewModel() {
        val uiState: StateFlow<ProfileListUiState> =
            repository
                .observeProfiles()
                .map { profiles ->
                    ProfileListUiState(
                        profiles = profiles,
                        isLoading = false,
                    )
                }.catch { throwable ->
                    emit(
                        ProfileListUiState(
                            isLoading = false,
                            errorMessage = throwable.message,
                        ),
                    )
                }.stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = ProfileListUiState(),
                )
    }
