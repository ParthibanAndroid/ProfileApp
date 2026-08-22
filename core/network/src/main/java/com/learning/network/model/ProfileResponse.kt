package com.learning.network.model

data class ProfileResponse(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val photoUrl: String?,
    val updatedAt: String,
    val version: Int,
)
