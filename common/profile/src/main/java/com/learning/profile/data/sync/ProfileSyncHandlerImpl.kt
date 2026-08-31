@file:RequiresApi(Build.VERSION_CODES.O)

package com.learning.profile.data.sync

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.learning.database.profile.SyncOperationDao
import com.learning.database.profile.SyncOperationEntity
import com.learning.database.profile.SyncOperationType
import com.learning.database.profile.SyncState
import com.learning.network.NetworkResult
import com.learning.network.model.CreateProfileRequest
import com.learning.network.model.UpdateProfileRequest
import com.learning.profile.data.local.ProfileLocalDataSource
import com.learning.profile.data.network.ProfileNetworkDataSource
import com.learning.sync.ProfileSyncHandler
import com.learning.sync.SyncResult
import java.io.File
import java.time.Instant
import javax.inject.Inject

class ProfileSyncHandlerImpl
    @Inject
    constructor(
        private val localDataSource: ProfileLocalDataSource,
        private val networkDataSource: ProfileNetworkDataSource,
        private val syncOperationDao: SyncOperationDao,
    ) : ProfileSyncHandler {
        companion object {
            private const val TAG = "ProfileSyncHandler"
        }

        override suspend fun sync(): SyncResult {
            val operations =
                syncOperationDao.getPendingOperations()

            if (operations.isEmpty()) {
                Log.d(TAG, "No pending operations")
                return SyncResult.Success
            }

            for (operation in operations) {
                Log.d(
                    TAG,
                    "Processing operation: $operation",
                )

                val result =
                    when (operation.operationType) {
                        SyncOperationType.CREATE -> {
                            syncCreate(operation)
                        }

                        SyncOperationType.UPDATE -> {
                            syncUpdate(operation)
                        }

                        SyncOperationType.UPLOAD_IMAGE -> {
                            syncUploadImage(operation)
                        }

                        SyncOperationType.DELETE -> {
                            syncDelete(operation)
                        }
                    }

                if (result != SyncResult.Success) {
                    return result
                }
            }

            return SyncResult.Success
        }

        private suspend fun syncCreate(operation: SyncOperationEntity): SyncResult {
            val profile =
                localDataSource.getProfile(id = operation.profileId)
                    ?: return SyncResult.Failure(message = "Profile not found: ${operation.profileId}")

            val request =
                CreateProfileRequest(
                    id = profile.id,
                    name = profile.name,
                    email = profile.email,
                    phone = profile.phone,
                    photoUrl = profile.photoUrl,
                )

            return when (val result = networkDataSource.createProfile(request = request)) {
                is NetworkResult.Success -> {
                    val syncProfile =
                        profile.copy(
                            syncState = SyncState.SYNCED,
                            version = result.data.version,
                            updatedAt = Instant.parse(result.data.updatedAt),
                        )

                    localDataSource.markProfileSyncedAndDeleteOperation(
                        profile = syncProfile,
                        operation = operation,
                    )

                    SyncResult.Success
                }

                is NetworkResult.HttpError -> {
                    SyncResult.Failure(message = "HTTP ${result.code}")
                }

                is NetworkResult.NetworkError -> {
                    SyncResult.Retry
                }

                is NetworkResult.UnknownError -> {
                    SyncResult.Retry
                }
            }
        }

        private suspend fun syncUpdate(operation: SyncOperationEntity): SyncResult {
            val profile =
                localDataSource.getProfile(
                    id = operation.profileId,
                ) ?: return SyncResult.Failure(
                    message = "Profile not found: ${operation.profileId}",
                )

            val request =
                UpdateProfileRequest(
                    name = profile.name,
                    email = profile.email,
                    phone = profile.phone,
                    photoUrl = profile.photoUrl,
                )

            return when (
                val result =
                    networkDataSource.updateProfile(
                        id = profile.id,
                        request = request,
                    )
            ) {
                is NetworkResult.Success -> {
                    val serverProfile = result.data

                    val syncedProfile =
                        profile.copy(
                            name = serverProfile.name,
                            email = serverProfile.email,
                            phone = serverProfile.phone,
                            photoUrl = serverProfile.photoUrl,
                            updatedAt =
                                Instant.parse(
                                    serverProfile.updatedAt,
                                ),
                            version = serverProfile.version,
                            syncState = SyncState.SYNCED,
                        )

                    localDataSource.markProfileSyncedAndDeleteOperation(
                        profile = syncedProfile,
                        operation = operation,
                    )

                    SyncResult.Success
                }

                is NetworkResult.HttpError -> {
                    SyncResult.Failure(
                        message = "HTTP ${result.code}",
                    )
                }

                is NetworkResult.NetworkError -> {
                    SyncResult.Retry
                }

                is NetworkResult.UnknownError -> {
                    SyncResult.Retry
                }
            }
        }

        private suspend fun syncUploadImage(operation: SyncOperationEntity): SyncResult {
            val filePath =
                operation.filePath
                    ?: return SyncResult.Failure(
                        message = "Image file path is missing",
                    )

            val file = File(filePath)

            if (!file.exists()) {
                return SyncResult.Failure(
                    message = "Image file does not exist: $filePath",
                )
            }

            return when (
                val result =
                    networkDataSource.uploadProfileImage(
                        id = operation.profileId,
                        file = file,
                    )
            ) {
                is NetworkResult.Success -> {
                    val profile =
                        localDataSource.getProfile(
                            id = operation.profileId,
                        ) ?: return SyncResult.Failure(
                            message = "Profile not found: ${operation.profileId}",
                        )

                    val serverProfile = result.data

                    val syncedProfile =
                        profile.copy(
                            photoUrl = serverProfile.photoUrl,
                            updatedAt = Instant.parse(serverProfile.updatedAt),
                            version = serverProfile.version,
                            syncState = SyncState.SYNCED,
                        )

                    localDataSource.markProfileSyncedAndDeleteOperation(
                        profile = syncedProfile,
                        operation = operation,
                    )

                    SyncResult.Success
                }

                is NetworkResult.HttpError -> {
                    SyncResult.Failure(
                        message = "HTTP ${result.code}",
                    )
                }

                is NetworkResult.NetworkError -> {
                    SyncResult.Retry
                }

                is NetworkResult.UnknownError -> {
                    SyncResult.Retry
                }
            }
        }

        private suspend fun syncDelete(operation: SyncOperationEntity): SyncResult {
            Log.d(TAG, "Deleting profile: ${operation.profileId}")

            return when (
                val result =
                    networkDataSource.deleteProfile(
                        id = operation.profileId,
                    )
            ) {
                is NetworkResult.Success -> {
                    localDataSource.deleteProfileAndOperation(
                        profileId = operation.profileId,
                        operation = operation,
                    )

                    SyncResult.Success
                }

                is NetworkResult.HttpError -> {
                    Log.e(TAG, "Delete failed: HTTP ${result.code}")

                    SyncResult.Failure(
                        message = "HTTP ${result.code}",
                    )
                }

                is NetworkResult.NetworkError -> {
                    Log.e(TAG, "Delete network error")

                    SyncResult.Retry
                }

                is NetworkResult.UnknownError -> {
                    Log.e(TAG, "Delete unknown error")

                    SyncResult.Retry
                }
            }
        }
    }
