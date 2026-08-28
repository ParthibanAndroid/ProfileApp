@file:RequiresApi(Build.VERSION_CODES.O)

package com.learning.profile.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.learning.database.profile.SyncOperationEntity
import com.learning.database.profile.SyncOperationType
import com.learning.database.profile.SyncState
import com.learning.network.NetworkResult
import com.learning.network.model.CreateProfileRequest
import com.learning.network.model.ProfileResponse
import com.learning.network.model.UpdateProfileRequest
import com.learning.profile.data.local.ProfileLocalDataSource
import com.learning.profile.data.mapper.toEntity
import com.learning.profile.data.mapper.toResponse
import com.learning.profile.data.network.ProfileNetworkDataSource
import com.learning.profile.domain.model.Profile
import com.learning.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

class ProfileRepositoryImpl
    @Inject
    constructor(
        private val localDataSource: ProfileLocalDataSource,
        private val networkDataSource: ProfileNetworkDataSource,
    ) : ProfileRepository {
        override fun observeProfile(id: String): Flow<ProfileResponse?> =
            localDataSource
                .observeProfile(id = id)
                .map { entity ->
                    entity?.toResponse()
                }

        override suspend fun refreshProfile(id: String): NetworkResult<Unit> =
            when (val result = networkDataSource.getProfile(id = id)) {
                is NetworkResult.Success -> {
                    localDataSource.insertProfile(result.data.toEntity())

                    NetworkResult.Success(Unit)
                }

                is NetworkResult.HttpError -> {
                    result
                }

                is NetworkResult.NetworkError -> {
                    result
                }

                is NetworkResult.UnknownError -> {
                    result
                }
            }

        override suspend fun createProfile(request: CreateProfileRequest): NetworkResult<ProfileResponse> {
            val profileId = UUID.randomUUID().toString()

            val profile =
                Profile(
                    id = profileId,
                    name = request.name,
                    email = request.email,
                    phone = request.phone,
                    photoUrl = request.photoUrl,
                    updatedAt = Instant.now(),
                )

            val operation =
                SyncOperationEntity(
                    profileId = profileId,
                    operationType = SyncOperationType.CREATE,
                )

            localDataSource.saveProfileAndQueueOperation(
                profile = profile.toEntity(syncState = SyncState.PENDING_CREATE),
                operation = operation,
            )

            return NetworkResult.Success(data = profile.toResponse())
        }

        override suspend fun updateProfile(
            id: String,
            request: UpdateProfileRequest,
        ): NetworkResult<ProfileResponse> {
            val profile =
                Profile(
                    id = id,
                    name = request.name,
                    email = request.email,
                    phone = request.phone,
                    photoUrl = request.photoUrl,
                    updatedAt = Instant.now(),
                )

            val operation =
                SyncOperationEntity(
                    profileId = id,
                    operationType = SyncOperationType.UPDATE,
                )

            localDataSource.saveProfileAndQueueOperation(
                profile = profile.toEntity(syncState = SyncState.PENDING_UPDATE),
                operation = operation,
            )

            return NetworkResult.Success(data = profile.toResponse())
        }

        override suspend fun uploadProfileImage(
            id: String,
            file: File,
        ): NetworkResult<ProfileResponse> {
            TODO("Implement")
        }

        override suspend fun deleteProfile(id: String): NetworkResult<Unit> {
            val entity = localDataSource.getProfile(id = id)

            entity?.let {
                val operation =
                    SyncOperationEntity(
                        profileId = id,
                        operationType = SyncOperationType.DELETE,
                    )

                localDataSource.saveProfileAndQueueOperation(
                    profile = entity.copy(syncState = SyncState.PENDING_DELETE),
                    operation = operation,
                )
            }

            return NetworkResult.Success(Unit)
        }
    }
