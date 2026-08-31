package com.learning.profile.domain.model

import java.time.Instant

data class Profile(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val photoUrl: String?,
    val updatedAt: Instant,
    val version: Long = 0,
)
