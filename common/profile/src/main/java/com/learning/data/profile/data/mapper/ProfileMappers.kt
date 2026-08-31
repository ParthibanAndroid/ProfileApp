@file:RequiresApi(Build.VERSION_CODES.O)

package com.learning.data.profile.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.learning.database.profile.ProfileEntity
import com.learning.database.profile.SyncState
import com.learning.network.model.ProfileResponse
import com.learning.data.profile.domain.model.Profile
import java.time.Instant

fun ProfileResponse.toEntity(): ProfileEntity =
    ProfileEntity(
        id = id,
        name = name,
        email = email,
        phone = phone,
        photoUrl = photoUrl,
        updatedAt = Instant.parse(updatedAt),
        version = version,
    )

fun ProfileEntity.toResponse(): ProfileResponse =
    ProfileResponse(
        id = id,
        name = name,
        email = email,
        phone = phone,
        photoUrl = photoUrl,
        updatedAt = updatedAt.toString(),
        version = version,
    )

fun ProfileResponse.toDomain(): Profile =
    Profile(
        id = id,
        name = name,
        email = email,
        phone = phone,
        photoUrl = photoUrl,
        updatedAt = Instant.parse(updatedAt),
        version = version,
    )

fun ProfileEntity.toDomain(): Profile =
    Profile(
        id = id,
        name = name,
        email = email,
        phone = phone,
        photoUrl = photoUrl,
        updatedAt = updatedAt,
        version = version,
    )

fun Profile.toEntity(
    syncState: SyncState = SyncState.SYNCED,
): ProfileEntity =
    ProfileEntity(
        id = id,
        name = name,
        email = email,
        phone = phone,
        photoUrl = photoUrl,
        updatedAt = updatedAt,
        version = version,
        syncState = syncState,
    )

fun Profile.toResponse(): ProfileResponse =
    ProfileResponse(
        id = id,
        name = name,
        email = email,
        phone = phone,
        photoUrl = photoUrl,
        updatedAt = updatedAt.toString(),
        version = version,
    )
