package com.learning.profile

sealed interface ProfileEvent {
    data class UpdateName(val name: String) : ProfileEvent
    data class UpdateEmail(val email: String) : ProfileEvent
    data class UpdatePhone(val phone: String) : ProfileEvent
    data class UpdatePhotoUrl(val photoUrl: String) : ProfileEvent
    data class GetProfile(val id: String) : ProfileEvent
    data class SaveProfile(val profile: String) : ProfileEvent
    data class UpdateProfile(val profile: String) : ProfileEvent
    data class DeleteProfile(val id: String) : ProfileEvent
}
