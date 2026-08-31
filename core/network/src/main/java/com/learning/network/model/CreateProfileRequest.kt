package com.learning.network.model

data class CreateProfileRequest(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val photoUrl: String? = null,
)
