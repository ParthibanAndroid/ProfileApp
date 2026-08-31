package com.learning.profile

import android.net.Uri

sealed interface ProfileEvent {
    data class NameChanged(
        val value: String,
    ) : ProfileEvent

    data class EmailChanged(
        val value: String,
    ) : ProfileEvent

    data class PhoneChanged(
        val value: String,
    ) : ProfileEvent

    data class PhotoUrlChanged(
        val value: String,
    ) : ProfileEvent

    data class ProfileImageSelected(
        val uri: Uri,
    ) : ProfileEvent

    data class LoadProfile(
        val profileId: String?,
    ) : ProfileEvent

    data object SaveClicked : ProfileEvent

    data object UpdateClicked : ProfileEvent

    data object DeleteClicked : ProfileEvent
}
