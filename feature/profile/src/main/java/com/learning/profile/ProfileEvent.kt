package com.learning.profile

import android.content.Context
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

    data object ProfileImageClicked : ProfileEvent

    data class ProfileImageSelected(
        val uri: Uri,
    ) : ProfileEvent

    data class SaveClicked(val context: Context) : ProfileEvent

    data class UpdateClicked(val context: Context) : ProfileEvent

    data object DeleteClicked : ProfileEvent
}
