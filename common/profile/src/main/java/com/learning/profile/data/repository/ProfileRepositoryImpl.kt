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
import com.learning.sync.ProfileSyncScheduler
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
        private val syncScheduler: ProfileSyncScheduler,
    ) : ProfileRepository {
        override fun observeProfile(id: String): Flow<ProfileResponse?> =
            localDataSource
                .observeProfile(id = id)
                .map { entity ->
                    entity?.toResponse()
                }

        override fun observeProfiles(): Flow<List<Profile>> = localDataSource.observeProfiles()

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

            syncScheduler.schedule()

            return NetworkResult.Success(data = profile.toResponse())
        }

        override suspend fun updateProfile(
            id: String,
            request: UpdateProfileRequest,
        ): NetworkResult<ProfileResponse> {
            val existingProfile =
                localDataSource.getProfile(id)
                    ?: return NetworkResult.UnknownError(
                        IllegalStateException("Profile not found: $id"),
                    )

            val updatedProfile =
                existingProfile.copy(
                    name = request.name,
                    email = request.email,
                    phone = request.phone,
                    photoUrl = request.photoUrl,
                    updatedAt = Instant.now(),
                )

            return when (existingProfile.syncState) {
                SyncState.PENDING_CREATE -> {
                    localDataSource.updatePendingCreateProfile(
                        profile = updatedProfile,
                    )

                    syncScheduler.schedule()

                    NetworkResult.Success(
                        data = updatedProfile.toResponse(),
                    )
                }

                SyncState.SYNCED -> {
                    val operation =
                        SyncOperationEntity(
                            profileId = id,
                            operationType = SyncOperationType.UPDATE,
                        )

                    localDataSource.updateProfileAndQueueOperation(
                        profile =
                            updatedProfile.copy(
                                syncState = SyncState.PENDING_UPDATE,
                            ),
                        operation = operation,
                    )

                    syncScheduler.schedule()

                    NetworkResult.Success(
                        data = updatedProfile.toResponse(),
                    )
                }

                SyncState.PENDING_UPDATE -> {
                    localDataSource.updatePendingUpdateProfile(
                        profile = updatedProfile,
                    )

                    syncScheduler.schedule()

                    NetworkResult.Success(
                        data = updatedProfile.toResponse(),
                    )
                }

                SyncState.PENDING_DELETE -> {
                    NetworkResult.UnknownError(
                        IllegalStateException(
                            "Cannot update a profile pending deletion",
                        ),
                    )
                }
            }
        }

        override suspend fun uploadProfileImage(
            id: String,
            file: File,
        ): NetworkResult<ProfileResponse> {
            val existingProfile =
                localDataSource.getProfile(id)
                    ?: return NetworkResult.UnknownError(
                        IllegalStateException("Profile not found locally: $id"),
                    )

            val updatedProfile =
                existingProfile.copy(
                    syncState =
                        if (existingProfile.syncState == SyncState.PENDING_CREATE) {
                            SyncState.PENDING_CREATE
                        } else {
                            SyncState.PENDING_UPDATE
                        },
                    updatedAt = Instant.now(),
                )

            val operation =
                SyncOperationEntity(
                    profileId = id,
                    operationType = SyncOperationType.UPLOAD_IMAGE,
                    filePath = file.absolutePath,
                )

            localDataSource.updateProfileAndQueueOperation(
                profile = updatedProfile,
                operation = operation,
            )

            localDataSource.replaceImageOperation(
                operation = operation,
            )

            syncScheduler.schedule()

            return NetworkResult.Success(
                data = updatedProfile.toResponse(),
            )
        }

        override suspend fun deleteProfile(id: String): NetworkResult<Unit> {
            val entity =
                localDataSource.getProfile(id = id)
                    ?: return NetworkResult.Success(Unit)

            when (entity.syncState) {
                SyncState.PENDING_CREATE -> {
                    val createOperation =
                        localDataSource.getOperation(
                            profileId = id,
                            operationType = SyncOperationType.CREATE,
                        )

                    if (createOperation != null) {
                        localDataSource.cancelPendingCreate(
                            profile = entity,
                            operation = createOperation,
                        )
                    }
                }

                SyncState.SYNCED -> {
                    val operation =
                        SyncOperationEntity(
                            profileId = id,
                            operationType = SyncOperationType.DELETE,
                        )

                    localDataSource.markProfilePendingDeleteAndQueueOperation(
                        profile =
                            entity.copy(
                                syncState = SyncState.PENDING_DELETE,
                            ),
                        operation = operation,
                    )

                    syncScheduler.schedule()
                }

                SyncState.PENDING_UPDATE -> {
                    val updateOperation =
                        localDataSource.getOperation(
                            profileId = id,
                            operationType = SyncOperationType.UPDATE,
                        )

                    val deleteOperation =
                        SyncOperationEntity(
                            profileId = id,
                            operationType = SyncOperationType.DELETE,
                        )

                    if (updateOperation != null) {
                        localDataSource.replaceUpdateWithDelete(
                            profile =
                                entity.copy(
                                    syncState = SyncState.PENDING_DELETE,
                                ),
                            updateOperation = updateOperation,
                            deleteOperation = deleteOperation,
                        )
                    } else {
                        localDataSource.markProfilePendingDeleteAndQueueOperation(
                            profile =
                                entity.copy(
                                    syncState = SyncState.PENDING_DELETE,
                                ),
                            operation = deleteOperation,
                        )
                    }

                    syncScheduler.schedule()
                }

                SyncState.PENDING_DELETE -> {
                    // Delete is already queued.
                    // Nothing to do.
                }
            }

            return NetworkResult.Success(Unit)
        }
    }
