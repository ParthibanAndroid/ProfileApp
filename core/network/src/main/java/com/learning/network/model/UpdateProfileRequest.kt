package com.learning.network.model

data class UpdateProfileRequest(
    val name: String,
    val email: String,
    val phone: String,
    val photoUrl: String? = null,
)
