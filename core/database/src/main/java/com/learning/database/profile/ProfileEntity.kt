package com.learning.database.profile

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val photoUrl: String?,
    val updatedAt: Instant,
    val version: Long,
    val syncState: SyncState = SyncState.SYNCED,
)
