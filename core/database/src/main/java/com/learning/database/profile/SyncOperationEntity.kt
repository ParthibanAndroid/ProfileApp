@file:RequiresApi(Build.VERSION_CODES.O)

package com.learning.database.profile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.UUID

@Entity(tableName = "sync_operations")
data class SyncOperationEntity(
    @PrimaryKey
    val operationId: String = UUID.randomUUID().toString(),
    val profileId: String,
    val operationType: SyncOperationType,
    val createdAt: Instant = Instant.now(),
    val retryCount: Int = 0,
)
